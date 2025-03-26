package community.Api.Post.Controller;

import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Api.Post.Service.PostService;
import community.Common.ApiResponse;
import community.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("")
    ApiResponse<PostResponse.CreatePostResponse> CreatePostController(@RequestBody PostRequest.CreatePostRequest request) {
        return new ApiResponse<>(postService.createPostService(request, JwtUtil.getAuthenticatedUserId()), "201");
    }

    @GetMapping("")
    ApiResponse<PostResponse.GetPostsResponse> GetPostsController(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int offset) {
        return new ApiResponse<>(postService.getPostsService(page, offset), "200");
    }

    @GetMapping("/{postId}")
    ApiResponse<PostResponse.GetPostResponse> GetPostController(@PathVariable String postId) {
        return new ApiResponse<>(postService.getPostService(postId, JwtUtil.getAuthenticatedUserId()), "200");
    }

    @PatchMapping("/{postId}")
    ApiResponse<String> PatchPostController(@PathVariable String postId, @RequestBody PostRequest.PatchPostRequest request) {
        return new ApiResponse<>(postService.patchPostService(request, JwtUtil.getAuthenticatedUserId(), postId), "200");
    }

    @DeleteMapping("/{postId}")
    ApiResponse<String> DeletePostController(@PathVariable String postId) {
        return new ApiResponse<>(postService.deletePostService(postId, JwtUtil.getAuthenticatedUserId()), "200");
    }

    //댓글관련
    @PostMapping("/{postId}/comments")
    ApiResponse<PostResponse.CreateCommentResponse> CreateCommentController(@PathVariable String postId, @RequestBody PostRequest.CreateCommentRequest request) {
        return new ApiResponse<>(postService.createCommentService(request, JwtUtil.getAuthenticatedUserId(), postId), "201");
    }

    @GetMapping("/{postId}/comments")
    ApiResponse<PostResponse.GetCommentsResponse> GetCommentsController(@PathVariable String postId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int offset) {
        return new ApiResponse<>(postService.getCommentsService(postId, page, offset), "200");
    }

    @PutMapping("/{postId}/comments/{commentId}")
    ApiResponse<String> PutCommentController(@PathVariable String postId, @PathVariable String commentId, @RequestBody PostRequest.PutCommentRequest request) {
        return new ApiResponse<>(postService.putCommentService(postId, request, JwtUtil.getAuthenticatedUserId(), commentId), "200");
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    ApiResponse<String> DeleteCommentController(@PathVariable String postId, @PathVariable String commentId) {
        return new ApiResponse<>(postService.deleteCommentService(postId, commentId, JwtUtil.getAuthenticatedUserId()), "200");
    }

    @PostMapping("/{postId}/likes")
    ApiResponse<String> DoLikeController(@PathVariable String postId) {
        return new ApiResponse<>(postService.doLikeService(postId, JwtUtil.getAuthenticatedUserId()), "200");
    }
}
