package community.Api.Post.Service;

import community.Api.Post.Converter.PostConverter;
import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Common.Enums.MessageCode;
import community.Model.Comment;
import community.Model.Post;
import community.Model.User;
import community.Model.UserPostLike;
import community.Repository.CommentRepository;
import community.Repository.PostRepository;
import community.Repository.UserPostLikeRepository;
import community.Repository.UserRepository;
import community.Util.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserPostLikeRepository userPostLikeRepository;
    private final EntityValidator entityValidator;
    private final VisitService visitService;

    @Override
    public PostResponse.CreatePostResponse createPostService(PostRequest.CreatePostRequest request, String userId) {


        User userJpa = entityValidator.getValidUserOrThrow(userId);

        String postId = "POST-" + UUID.randomUUID();

        Post postJpa = Post.builder()
                .postId(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .contentImage(request.getContentImage())
                .commentCount(0)
                .likeCount(0)
                .user(userJpa)
                .build();
        postRepository.save(postJpa);


        postRepository.save(postJpa);

        return PostConverter.toCreatePostResponse(postJpa);
    }

    public PostResponse.GetPostsResponse getPostsService(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // JPA 페이징 적용된 조회
        Page<Post> postPage = postRepository.findAll(pageable);

        // 삭제되지 않은 게시물만 필터링
        List<PostResponse.PostsItem> responses = postPage.getContent().stream()
                .filter(post -> post.getDeletedAt() == null)
                .map(post -> {
                    User user = post.getUser();
                    return PostConverter.toPostsItem(post, user.getNickname(), user.getUserProfile());
                }).toList();


        return PostConverter.toGetPostsResponse(responses);
    }


    public PostResponse.GetPostResponse getPostService(String postId, String userId) {
        User user = entityValidator.getValidUserOrThrow(userId);
        Post post = entityValidator.getValidPostOrThrow(postId);

        Optional<User> postWriterOptional = userRepository.findById(post.getUser().getUserId());
        User writer = postWriterOptional.get();


        Optional<UserPostLike> userPostLike = userPostLikeRepository.findByPostAndUser(post, user);

        Boolean is_like = false;
        if (userPostLike.isPresent() && userPostLike.get().isLike()) {
            is_like = true;
        }

        visitService.increaseVisitCount(post);

        return PostConverter.toGetPostResponse(post, writer.getNickname(), writer.getUserProfile(), is_like);
    }

    @Transactional
    public String patchPostService(PostRequest.PatchPostRequest request, String userId, String postId) {

        User user = entityValidator.getValidUserOrThrow(userId);
        Post post = entityValidator.getValidPostOrThrow(postId);

        entityValidator.validatePostOwner(post, user);
        post.setTitle(request.getTitle() != null ? request.getTitle() : post.getTitle());
        post.setContent(request.getContent() != null ? request.getContent() : post.getContent());
        post.setContentImage(request.getContentImage() != null ? request.getContentImage() : post.getContentImage());
        return MessageCode.POST_UPDATED.getMessage();
    }

    @Transactional
    public String deletePostService(String postId, String userId) {

        User user = entityValidator.getValidUserOrThrow(userId);
        Post post = entityValidator.getValidPostOrThrow(postId);
        entityValidator.validatePostOwner(post, user);
        post.setDeletedAt(OffsetDateTime.now());
        return MessageCode.POST_DELETED.getMessage();
    }

    @Transactional
    public PostResponse.CreateCommentResponse createCommentService(PostRequest.CreateCommentRequest request, String userId, String postId) {

        User user = entityValidator.getValidUserOrThrow(userId);
        Post post = entityValidator.getValidPostOrThrow(postId);
        //댓글 수 증가

        increaseCommentCount(post);

        String commentId = "COMMENT-" + UUID.randomUUID();
        Comment commentJpa = Comment.builder()
                .commentId(commentId)
                .content(request.getContent())
                .user(user)
                .post(post).build();

        commentRepository.save(commentJpa);
        return PostConverter.toCreateCommentResponse(commentJpa);
    }

    public PostResponse.GetCommentsResponse getCommentsService(String postId, int page, int offset) {

        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.DESC, "createdAt"));

        Post post = entityValidator.getValidPostOrThrow(postId);
        // 특정 게시글의 댓글을 페이징하여 조회
        Page<Comment> commentPage = commentRepository.findAllByPost(post, pageable);

        // DTO로 변환
        List<PostResponse.CommentItem> comments = commentPage.getContent().stream()
                .map(PostConverter::toCommentItem).toList();

        return PostConverter.toGetCommentsResponse(comments);

    }

    @Transactional
    public String putCommentService(String postId, PostRequest.PutCommentRequest request, String userId, String commentId) {

        User user = entityValidator.getValidUserOrThrow(userId);

        Comment comment = entityValidator.getValidCommentOrThrow(commentId);

        entityValidator.validateCommentOwner(comment, user);

        String content = request.getContent() != null ? request.getContent() : comment.getContent();
        comment.setContent(content);
        return MessageCode.COMMENT_UPDATED.getMessage();

    }

    @Transactional
    public String deleteCommentService(String postId, String commentId, String userId) {

        User user = entityValidator.getValidUserOrThrow(userId);
        Post post = entityValidator.getValidPostOrThrow(postId);

        Comment comment = entityValidator.getValidCommentOrThrow(commentId);

        entityValidator.validateCommentOwner(comment, user);

        decreaseCommentCount(post);
        commentRepository.delete(comment);
        return MessageCode.COMMENT_DELETED.getMessage();
    }

    @Transactional
    public String doLikeService(String postId, String userId) {

        User user = entityValidator.getValidUserOrThrow(userId);
        Post post = entityValidator.getValidPostOrThrow(postId);

        System.out.println("Before like count = " + post.getLikeCount());

        Optional<UserPostLike> likeOptional = userPostLikeRepository.findByPostAndUser(post, user);
        UserPostLike userPostLike;

        if (likeOptional.isEmpty()) {
            increaseLikeCount(post); // count + 1
            userPostLike = UserPostLike.builder()
                    .likeId("LIKE-" + UUID.randomUUID())
                    .user(user)
                    .post(post)
                    .isLike(true)
                    .build();
            userPostLikeRepository.save(userPostLike);

        } else {
            userPostLike = likeOptional.get();
            if (!userPostLike.isLike()) {
                increaseLikeCount(post);
            } else {
                decreaseLikeCount(post);
            }
            changeIsLike(userPostLike);
        }

        System.out.println("After like count = " + post.getLikeCount());

        
        return MessageCode.LIKE_UPDATED.getMessage();
    }


    @Transactional
    public void increaseVisitCount(Post post) {
        post.increaseVisitCount();
    }

    @Transactional
    public void increaseCommentCount(Post post) {
        post.increaseCommentCount();
    }

    @Transactional
    public void decreaseCommentCount(Post post) {
        post.decreaseCommentCount();
    }

    @Transactional
    public void increaseLikeCount(Post post) {
        post.increaseLikeCount();
    }

    @Transactional
    public void decreaseLikeCount(Post post) {
        post.decreaseLikeCount();
    }

    @Transactional
    public void changeIsLike(UserPostLike userPostLike) {
        userPostLike.changeIsLike();
    }

}
