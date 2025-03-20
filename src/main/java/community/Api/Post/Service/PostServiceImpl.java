package community.Api.Post.Service;

import community.Api.Post.Converter.PostConverter;
import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Exception.PostException.PostException;
import community.Exception.UserException.UserException;
import community.Model.JdbcModel.CommentJdbc;
import community.Model.JdbcModel.PostJdbc;
import community.Model.JdbcModel.UserJdbc;

import community.Model.JpaModel.CommentJpa;
import community.Model.JpaModel.PostJpa;
import community.Model.JpaModel.UserJpa;
import community.Model.JpaModel.UserPostLikeJpa;
import community.Repository.RepositoryJpa.CommentRepositoryJpa;
import community.Repository.RepositoryJpa.PostRepositoryJpa;
import community.Repository.RepositoryJpa.UserPostLikeRepositoryJpa;
import community.Repository.RepositoryJpa.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
//    private final UserRepositoryJdbc userRepository;
//    private final PostRepositoryJdbc postRepository;
//    private final CommentRepositoryJdbc commentRepository;
//    private final UserPostLikeRepositoryJdbc userPostLikeRepository;
    private final UserRepositoryJpa userRepository;
    private final PostRepositoryJpa postRepository;
    private final CommentRepositoryJpa commentRepository;
    private final UserPostLikeRepositoryJpa userPostLikeRepository;
    @Override
    public PostResponse.CreatePostResponse CreatePostService(PostRequest.CreatePostRequest request, String userId) {
        Optional<UserJpa> userJpaOptional = userRepository.findById(userId);

        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        UserJpa userJpa = userJpaOptional.get();

        String postId= "POST-"+ UUID.randomUUID();

        PostJpa postJpa= PostJpa.builder()
                .postId(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .contentImage(request.getContentImage())
                .commentCount(0)
                .likeCount(0)
                .user(userJpa)
                .build();
        postRepository.save(postJpa);

//        PostJdbc postJdbc = new PostJdbc(
//                postId,
//                request.getTitle(),
//                request.getContent(),
//                request.getContentImage(),
//                0,
//                0,
//                0,
//                userId,
//                null
//        );


//        postJdbc.setCreatedAt(OffsetDateTime.now());
//        postJdbc.setUpdatedAt(OffsetDateTime.now());

        postRepository.save(postJpa);

        return PostConverter.toCreatePostResponse(postJpa);
    }
    public PostResponse.GetPostsResponse GetPostsService(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // JPA 페이징 적용된 조회
        Page<PostJpa> postPage = postRepository.findAll(pageable);

        // 삭제되지 않은 게시물만 필터링
        List<PostResponse.PostsItem> responses = postPage.getContent().stream()
                .filter(post -> post.getDeletedAt() == null)
                .map(post -> {
                    log.info(post.getTitle());
                    UserJpa user = userRepository.findById(post.getUser().getUserId())
                            .orElseThrow(() -> new UserException.UserNotFoundException("유저를 찾을 수 없습니다."));
                    return PostConverter.toPostsItem(post, user.getNickname(), user.getUserProfile());
                }).toList();


        return PostConverter.toGetPostsResponse(responses);
    }
    @Transactional
    public PostResponse.GetPostResponse GetPostService(String postId,String userId){
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJpa post=postJpaOptional.get();
        Optional<UserJpa> postWriterJpaOptional =userRepository.findById(post.getUser().getUserId());
        UserJpa writer = postWriterJpaOptional.get();
        //유저가 좋아요를 눌렀는지 확인
        Optional<UserJpa> userJpaOptional =userRepository.findById(userId);
        if(userJpaOptional.isEmpty()||userJpaOptional.get().getDeletedAt()!=null){
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        UserJpa user = userJpaOptional.get();

        Optional<UserPostLikeJpa>userPostLikeJpa=userPostLikeRepository.findByPostAndUser(post,user);

        Boolean is_like=false;
        if(userPostLikeJpa.isPresent()&&userPostLikeJpa.get().isLike()){
            is_like=true;
        }

        int visitCount=post.getVisitCount()+1;
        post.setVisitCount(visitCount);

        return PostConverter.toGetPostResponse(post, writer.getNickname(), writer.getUserProfile(),is_like);
    }
    @Transactional
    public String PatchPostService(PostRequest.PatchPostRequest request, String userId, String postId) {
        Optional<UserJpa> userJpaOptional=userRepository.findById(userId);
        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa userJpa=userJpaOptional.get();
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJpa postJpa=postJpaOptional.get();
        if(!userJpa.getUserId().equals(postJpa.getUser().getUserId())){
            throw new PostException.PostNotMatchUserException("해당 게시물의 작성자가 아닙니다.");
        }
        postJpa.setTitle(request.getTitle()!=null ? request.getTitle() : postJpa.getTitle());
        postJpa.setContent(request.getContent()!=null ? request.getContent() : postJpa.getContent());
        postJpa.setContentImage(request.getContentImage()!=null ? request.getContentImage() : postJpa.getContentImage());
        return "게시물 수정에 성공했습니다.";
    }
    @Transactional
    public String DeletePostService(String postId,String userId) {
        Optional<UserJpa> userJpaOptional=userRepository.findById(userId);
        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa user=userJpaOptional.get();
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJpa post=postJpaOptional.get();
        if(!user.getUserId().equals(post.getUser().getUserId())){
            throw new PostException.PostNotMatchUserException("해당 게시물의 작성자가 아닙니다.");
        }
        post.setDeletedAt(OffsetDateTime.now());
        return "삭제에 성공했습니다";
    }
    @Transactional
    public PostResponse.CreateCommentResponse CreateCommentService(PostRequest.CreateCommentRequest request, String userId,String postId){
        Optional<UserJpa> userJpaOptional=userRepository.findById(userId);
        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa user=userJpaOptional.get();
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJpa post=postJpaOptional.get();
        //댓글 수 증가

        int commentCount=post.getCommentCount()+1;
        post.setCommentCount(commentCount);

        String commentId="COMMENT-"+ UUID.randomUUID();
        CommentJpa commentJpa = CommentJpa.builder()
                .commentId(commentId)
                .content(request.getContent())
                .user(user)
                .post(post).build();

        commentRepository.save(commentJpa);
        return PostConverter.toCreateCommentResponse(commentJpa);
    }
    public PostResponse.GetCommentsResponse GetCommentsService(String postId,int page, int offset){

        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.DESC, "createdAt"));
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()||postJpaOptional.get().getDeletedAt()!=null){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJpa post=postJpaOptional.get();
        // 특정 게시글의 댓글을 페이징하여 조회
        Page<CommentJpa> commentPage = commentRepository.findAllByPost(post, pageable);

        // DTO로 변환
        List<PostResponse.CommentItem> comments = commentPage.getContent().stream()
                .map(PostConverter::toCommentItem).toList();

        return PostConverter.toGetCommentsResponse(comments);

    }
    @Transactional
    public String PutCommentService(String postId, PostRequest.PutCommentRequest request, String userId,String commentId) {
        Optional<UserJpa> userJpaOptional=userRepository.findById(userId);
        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa user=userJpaOptional.get();
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        Optional<CommentJpa>commentJpaOptional=commentRepository.findById(commentId);
        if(commentJpaOptional.isEmpty()){
            throw new PostException.PostNotFoundException("해당 댓글을 찾을 수 없습니다.");
        }
        CommentJpa comment=commentJpaOptional.get();
        if(!user.getUserId().equals(comment.getUser().getUserId())){
            throw new PostException.CommentNotMatchUserException("해당 댓글의 작성자가 아닙니다.");
        }

        String content=request.getContent()!=null ? request.getContent() : comment.getContent();
        comment.setContent(content);
        return "댓글 수정에 성공했습니다.";

    }
    @Transactional
    public String DeleteCommentService(String postId, String commentId, String userId) {
        Optional<UserJpa> userJpaOptional=userRepository.findById(userId);
        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa user=userJpaOptional.get();
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        Optional<CommentJpa>commentJpaOptional=commentRepository.findById(commentId);
        if(commentJpaOptional.isEmpty()){
            throw new PostException.PostNotFoundException("해당 댓글을 찾을 수 없습니다.");
        }
        CommentJpa comment=commentJpaOptional.get();
        if(!user.getUserId().equals(comment.getUser().getUserId())){
            throw new PostException.CommentNotMatchUserException("해당 댓글의 작성자가 아닙니다.");
        }

        PostJpa post=postJpaOptional.get();
        int commentCount=post.getCommentCount()-1;
        post.setCommentCount(commentCount);
        commentRepository.delete(comment);
        return "댓글 삭제에 성공했습니다.";
    }
    @Transactional
    public String DoLikeService(String postId, String userId) {
        Optional<UserJpa> userJpaOptional=userRepository.findById(userId);
        if (userJpaOptional.isEmpty() || userJpaOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa user=userJpaOptional.get();
        Optional<PostJpa> postJpaOptional=postRepository.findById(postId);
        if(postJpaOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJpa post=postJpaOptional.get();
        Optional<UserPostLikeJpa> likeOptional=userPostLikeRepository.findByPostAndUser(post,user);
        UserPostLikeJpa userPostLikeJpa;
        int likeCount=post.getLikeCount();
        if(likeOptional.isEmpty()){
            String likeId="LIKE-"+ UUID.randomUUID();
            likeCount++;
            userPostLikeJpa=UserPostLikeJpa.builder()
                    .likeId(likeId)
                    .user(user)
                    .post(post)
                    .isLike(true)
                    .build();
            userPostLikeRepository.save(userPostLikeJpa);
        }else{
            userPostLikeJpa=likeOptional.get();
            if(!userPostLikeJpa.isLike()){
                likeCount+=1;
                userPostLikeJpa.setLike(true);
            }else{
                likeCount-=1;
                userPostLikeJpa.setLike(false);
            }
        }



        post.setLikeCount(likeCount);

        return "좋아요가 업데이트 되었습니다";
    }
}
