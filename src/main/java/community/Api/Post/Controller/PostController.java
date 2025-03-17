package community.Api.Post.Controller;

import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Api.Post.Service.PostService;
import community.Common.ApiResponse;
import community.utill.JwtUtil;
import lombok.Getter;
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
        return new ApiResponse<>(postService.CreatePostService(request, JwtUtil.getAuthenticatedUserId()),"201");
    }
    @GetMapping("")
    ApiResponse<PostResponse.GetPostsResponse> GetPostsController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int offset) {
        return new ApiResponse<>(postService.GetPostsService(page, offset), "200");
    }
    @GetMapping("/{postId}")
    ApiResponse<PostResponse.GetPostResponse> GetPostController(@PathVariable String postId) {
        return new ApiResponse<>(postService.GetPostService(postId), "200");
    }
    @PatchMapping("/{postId}")
    ApiResponse<String> PatchPostController(@PathVariable String postId, @RequestBody PostRequest.UpdatePostRequest request) {
        return new ApiResponse<>(postService.PatchPostService(request,JwtUtil.getAuthenticatedUserId(), postId), "200");
    }
    @DeleteMapping("/{postId}")
    ApiResponse<String> DeletePostController(@PathVariable String postId) {
        return new ApiResponse<>(postService.DeletePostService(postId,JwtUtil.getAuthenticatedUserId()), "200");
    }

}
