package community.Api.Post.Service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private UserPostLikeRepository userPostLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .userId("USER-123")
                .nickname("tester")
                .userProfile("profile.png")
                .build();

        post = Post.builder()
                .postId("POST-123")
                .title("old title")
                .content("old content")
                .contentImage("old.png")
                .user(user)
                .build();
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_success() {
        // given
        PostRequest.CreatePostRequest request = PostRequest.CreatePostRequest.builder()
                .title("new title")
                .content("new content")
                .contentImage("new.png")
                .build();

        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PostResponse.CreatePostResponse response = postService.createPostService(request, "USER-123");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).startsWith("POST-");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void getPost_success() {
        // given
        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);
        when(userRepository.findById("USER-123")).thenReturn(Optional.of(user));

        // when
        PostResponse.GetPostResponse response = postService.getPostService("POST-123", "USER-123");
        when(userPostLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.empty());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("old title");
        assertThat(response.getUserName()).isEqualTo("tester");
        verify(visitService).increaseVisitCount(post);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void patchPost_success() {
        // given
        PostRequest.PatchPostRequest request = PostRequest.PatchPostRequest.builder()
                .title("patched title")
                .content(null) // 기존 값 유지
                .contentImage("patched.png")
                .build();

        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);

        // when
        String result = postService.patchPostService(request, "USER-123", "POST-123");

        // then
        assertThat(result).isEqualTo(MessageCode.POST_UPDATED.getMessage());
        assertThat(post.getTitle()).isEqualTo("patched title");
        assertThat(post.getContent()).isEqualTo("old content"); // 기존 유지
        assertThat(post.getContentImage()).isEqualTo("patched.png");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_success() {
        // given
        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);

        // when
        String result = postService.deletePostService("POST-123", "USER-123");

        // then
        assertThat(result).isEqualTo(MessageCode.POST_DELETED.getMessage());
        assertThat(post.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 삭제되지 않은 글만 반환")
    void getPosts_success() {
        Post deletedPost = Post.builder()
                .postId("POST-2")
                .title("deleted")
                .deletedAt(OffsetDateTime.now())
                .user(user)
                .build();

        List<Post> posts = List.of(post, deletedPost);
        Page<Post> page = new PageImpl<>(posts);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);

        PostResponse.GetPostsResponse response = postService.getPostsService(0, 10);

        assertThat(response.getPosts()).hasSize(1); // 삭제된 1개 제외
        assertThat(response.getPosts().get(0).getPostId()).isEqualTo("POST-123");
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_success() {
        PostRequest.CreateCommentRequest request = PostRequest.CreateCommentRequest.builder()
                .content("댓글 내용")
                .build();

        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);

        PostResponse.CreateCommentResponse response = postService.createCommentService(request, "USER-123", "POST-123");

        assertThat(response).isNotNull();
        assertThat(response.getCommentId()).startsWith("COMMENT-");
    }

    @Test
    @DisplayName("댓글 수정 성공 - content null 시 기존 값 유지")
    void putComment_success() {
        Comment comment = Comment.builder()
                .commentId("COMMENT-1")
                .content("기존 댓글")
                .user(user)
                .build();

        PostRequest.PutCommentRequest request = PostRequest.PutCommentRequest.builder()
                .content(null) // null 처리 확인
                .build();

        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidCommentOrThrow("COMMENT-1")).thenReturn(comment);

        String result = postService.putCommentService("POST-123", request, "USER-123", "COMMENT-1");

        assertThat(result).isEqualTo(MessageCode.COMMENT_UPDATED.getMessage());
        assertThat(comment.getContent()).isEqualTo("기존 댓글");
    }

    @Test
    @DisplayName("댓글 수정 성공 - content 존재 시 갱신")
    void putComment_withNewContent_success() {
        Comment comment = Comment.builder()
                .commentId("COMMENT-2")
                .content("기존 댓글")
                .user(user)
                .build();

        PostRequest.PutCommentRequest request = PostRequest.PutCommentRequest.builder()
                .content("새 댓글")
                .build();

        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidCommentOrThrow("COMMENT-2")).thenReturn(comment);

        String result = postService.putCommentService("POST-123", request, "USER-123", "COMMENT-2");

        assertThat(result).isEqualTo(MessageCode.COMMENT_UPDATED.getMessage());
        assertThat(comment.getContent()).isEqualTo("새 댓글");
    }

    @Test
    @DisplayName("댓글 삭제하기")
    void deleteComment_success() {

        Comment comment = Comment.builder()
                .commentId("COMMENT-2")
                .content("기존 댓글")
                .user(user)
                .build();


        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);
        when(entityValidator.getValidCommentOrThrow("COMMENT-2")).thenReturn(comment);

        String result = postService.deleteCommentService("POST-123", "USER-123", "COMMENT-2");
        assertThat(result).isEqualTo(MessageCode.COMMENT_DELETED.getMessage());

    }

    @Test
    @DisplayName("좋아요 처음 누르기")
    void doLike_firstTime_success() {
        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);
        when(userPostLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.empty());

        String result = postService.doLikeService("POST-123", "USER-123");

        assertThat(result).isEqualTo(MessageCode.LIKE_UPDATED.getMessage());
        verify(userPostLikeRepository).save(any());
    }

    @Test
    @DisplayName("좋아요 두번째 누르기")
    void doLike_secondTime_success() {
        UserPostLike userPostLike = UserPostLike.builder()
                .likeId("LIKE-001").post(post).user(user).isLike(true).build();

        when(entityValidator.getValidUserOrThrow("USER-123")).thenReturn(user);
        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);
        when(userPostLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(userPostLike));
        String result = postService.doLikeService("POST-123", "USER-123");
        assertThat(result).isEqualTo(MessageCode.LIKE_UPDATED.getMessage());

        verify(userPostLikeRepository).findByPostAndUser(post, user);
    }


    @Test
    @DisplayName("댓글 목록 조회 - 정상 페이징")
    void getComments_success() {
        Comment comment1 = Comment.builder()
                .commentId("COMMENT-1")
                .content("댓글1")
                .user(user)
                .post(post)
                .build();

        List<Comment> comments = List.of(comment1);
        Page<Comment> page = new PageImpl<>(comments);

        when(entityValidator.getValidPostOrThrow("POST-123")).thenReturn(post);
        when(commentRepository.findAllByPost(eq(post), any(Pageable.class))).thenReturn(page);

        PostResponse.GetCommentsResponse response = postService.getCommentsService("POST-123", 0, 10);

        assertThat(response).isNotNull();
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getComments().get(0).getContent()).isEqualTo("댓글1");
    }

}
