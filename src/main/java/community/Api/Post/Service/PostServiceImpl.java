package community.Api.Post.Service;

import community.Api.Post.Converter.PostConverter;
import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Exception.PostException.PostException;
import community.Exception.UserException.UserException;
import community.Model.JdbcModel.PostJdbc;
import community.Model.JdbcModel.UserJdbc;
import community.RepositoryJdbc.PostRepositoryJdbc;
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
    public PostResponse.GetPostResponse GetPostService(String postId){
        Optional<PostJdbc> postJdbcOptional=postRepository.findByPostId(postId);
        if(postJdbcOptional.isEmpty()){
            throw  new PostException.PostNotFoundException("게시물을 찾을 수 없거나 삭제된 게시물입니다.");
        }
        PostJdbc post=postJdbcOptional.get();
        Optional<UserJdbc> userJdbcOptional=userRepository.findByUserId(post.getUserId());
        UserJdbc user=userJdbcOptional.get();
        return PostConverter.toGetPostResponse(post,user.getNickname(),user.getUserProfile());
    }
    public String PatchPostService(PostRequest.UpdatePostRequest request, String userId,String postId) {
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
}
