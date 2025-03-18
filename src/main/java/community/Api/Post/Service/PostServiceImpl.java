package community.Api.Post.Service;

import community.Api.Post.Converter.PostConverter;
import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Exception.PostException.PostException;
import community.Exception.UserException.UserException;
import community.Model.JdbcModel.CommentJdbc;
import community.Model.JdbcModel.PostJdbc;
import community.Model.JdbcModel.UserJdbc;
import community.Model.JdbcModel.UserPostLikeJdbc;
import community.RepositoryJdbc.CommentRepositoryJdbc;
import community.RepositoryJdbc.PostRepositoryJdbc;
import community.RepositoryJdbc.UserPostLikeRepositoryJdbc;
import community.RepositoryJdbc.UserRepositoryJdbc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final UserRepositoryJdbc userRepository;
    private final PostRepositoryJdbc postRepository;
    private final CommentRepositoryJdbc commentRepository;
    private final UserPostLikeRepositoryJdbc userPostLikeRepository;
    @Override
    public PostResponse.CreatePostResponse CreatePostService(PostRequest.CreatePostRequest request, String userId) {
        Optional<UserJdbc> user = userRepository.findByUserId(userId);

        if (user.isEmpty() || user.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        UserJdbc userJdbc = user.get();

        String postId= "POST-"+ UUID.randomUUID();

        PostJdbc postJdbc = new PostJdbc(
                postId,
                request.getTitle(),
                request.getContent(),
                request.getContentImage(),
                0,
                0,
                0,
                userId,
                null
        );


        postJdbc.setCreatedAt(OffsetDateTime.now());
        postJdbc.setUpdatedAt(OffsetDateTime.now());

        postRepository.save(postJdbc);

        return PostConverter.toCreatePostResponse(postJdbc);
    }
    public PostResponse.GetPostsResponse GetPostsService(int page, int offset){
        List<String> posts=postRepository.findAllPostIds(page, offset);
        List<PostResponse.PostsItem> responses=posts.stream().map(response ->{
            Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(response);
            PostJdbc postJdbc=postJdbcOptional.get();
            Optional<UserJdbc> userJdbc=userRepository.findByUserId(postJdbc.getUserId());
            UserJdbc user=userJdbc.get();
            return PostConverter.toPostsItem(postJdbc,user.getNickname(),user.getUserProfile());
        }).toList();

        return PostConverter.toGetPostsResponse(responses);
    }
    public PostResponse.GetPostResponse GetPostService(String postId,String userId){
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJdbc post=postJdbcOptional.get();
        Optional<UserJdbc> postWriterJdbcOptional =userRepository.findByUserId(post.getUserId());
        UserJdbc writer = postWriterJdbcOptional.get();
        Optional<Boolean>userPostLikeJdbc=userPostLikeRepository.findByPostAndUser(postId,userId);

        Boolean is_like=false;
        if(userPostLikeJdbc.isPresent()&&userPostLikeJdbc.get()){
            is_like=true;
        }
        int visitCount=post.getVisitCount()+1;
        postRepository.updateVisitCount(postId, visitCount);
        return PostConverter.toGetPostResponse(post, writer.getNickname(), writer.getUserProfile(),is_like);
    }
    public String PatchPostService(PostRequest.PatchPostRequest request, String userId, String postId) {
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(userId);
        if (userJdbcOptional.isEmpty() || userJdbcOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJdbc user=userJdbcOptional.get();
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJdbc post=postJdbcOptional.get();
        if(!user.getUserId().equals(post.getUserId())){
            throw new PostException.PostNotMatchUserException("해당 게시물의 작성자가 아닙니다.");
        }

        //수정 로직 시작
        String title = request.getTitle()!=null ? request.getTitle() : post.getTitle();
        String content = request.getContent()!=null ? request.getContent() : post.getContent();
        String contentImage= request.getContentImage()!=null ? request.getContentImage() : post.getContentImage();
        postRepository.updatePost(postId, title, content, contentImage);
        return "게시물 수정에 성공했습니다.";
    }
    public String DeletePostService(String postId,String userId) {
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(userId);
        if (userJdbcOptional.isEmpty() || userJdbcOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJdbc user=userJdbcOptional.get();
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJdbc post=postJdbcOptional.get();
        if(!user.getUserId().equals(post.getUserId())){
            throw new PostException.PostNotMatchUserException("해당 게시물의 작성자가 아닙니다.");
        }
        postRepository.deletePost(postId);
        return "삭제에 성공했습니다";
    }
    public PostResponse.CreateCommentResponse CreateCommentService(PostRequest.CreateCommentRequest request, String userId,String postId){
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(userId);
        if (userJdbcOptional.isEmpty() || userJdbcOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJdbc user=userJdbcOptional.get();
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        //댓글 수 증가
        PostJdbc post=postJdbcOptional.get();
        int commentCount=post.getCommentCount()+1;
        postRepository.updateCommentCount(postId, commentCount);

        String commentId="COMMENT-"+ UUID.randomUUID();
        CommentJdbc commentJdbc=new CommentJdbc(commentId,request.getContent(),postId,user.getUserId());
        commentJdbc.setCreatedAt(OffsetDateTime.now());
        commentJdbc.setUpdatedAt(OffsetDateTime.now());
        commentRepository.save(commentJdbc);
        return PostConverter.toCreateCommentResponse(commentJdbc);
    }
    public PostResponse.GetCommentsResponse GetCommentsService(String postId,int page, int offset){
        List<CommentJdbc> commentJdbc=commentRepository.findAllByPostId(postId,page,offset);
        List<PostResponse.CommentItem> commentItems=commentJdbc.stream().map(comment->{
            Optional<UserJdbc>userOptional=userRepository.findByUserId(comment.getUserId());
            log.info(comment.getContent());
            if(userOptional.isEmpty()||userOptional.get().getDeletedAt()!=null){
                return null;
            }
            log.info(comment.getContent());
            UserJdbc user=userOptional.get();
            return PostConverter.toCommentItem(comment,user);
        }).filter(Objects::nonNull).toList();
        return PostConverter.toGetCommentsResponse(commentItems);
    }
    public String PutCommentService(String postId, PostRequest.PutCommentRequest request, String userId,String commentId) {
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(userId);
        if (userJdbcOptional.isEmpty() || userJdbcOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJdbc user=userJdbcOptional.get();
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        Optional<CommentJdbc>commentJdbcOptional=commentRepository.findByCommentId(commentId);
        if(commentJdbcOptional.isEmpty()){
            throw new PostException.PostNotFoundException("해당 댓글을 찾을 수 없습니다.");
        }
        CommentJdbc comment=commentJdbcOptional.get();
        if(!user.getUserId().equals(comment.getUserId())){
            throw new PostException.CommentNotMatchUserException("해당 댓글의 작성자가 아닙니다.");
        }

        String content=request.getContent()!=null ? request.getContent() : comment.getContent();
        commentRepository.updateComment(commentId, content);
        return "댓글 수정에 성공했습니다.";

    }
    public String DeleteCommentService(String postId, String commentId, String userId) {
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(userId);
        if (userJdbcOptional.isEmpty() || userJdbcOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJdbc user=userJdbcOptional.get();
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }

        Optional<CommentJdbc>commentJdbcOptional=commentRepository.findByCommentId(commentId);
        if(commentJdbcOptional.isEmpty()){
            throw new PostException.PostNotFoundException("해당 댓글을 찾을 수 없습니다.");
        }
        CommentJdbc comment=commentJdbcOptional.get();
        if(!user.getUserId().equals(comment.getUserId())){
            throw new PostException.CommentNotMatchUserException("해당 댓글의 작성자가 아닙니다.");
        }
        PostJdbc post=postJdbcOptional.get();
        int commentCount=post.getCommentCount()-1;
        postRepository.updateCommentCount(postId, commentCount);
        commentRepository.deleteByCommentId(commentId);
        return "댓글 삭제에 성공했습니다.";
    }
    public String DoLikeService(String postId, String userId) {
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(userId);
        if (userJdbcOptional.isEmpty() || userJdbcOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        Optional<Boolean> likeOptional=userPostLikeRepository.findByPostAndUser(postId, userId);
        int likeCount=postJdbcOptional.get().getLikeCount();
        if(likeOptional.isEmpty()||!likeOptional.get()){
            likeCount++;
        }else{
            likeCount--;
        }
        postRepository.updateLikeCount(postId, likeCount);
        userPostLikeRepository.toggleLike(postId, userId);
        return "좋아요가 업데이트 되었습니다";
    }
}
