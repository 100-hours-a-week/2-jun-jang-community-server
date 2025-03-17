package community.Api.Post.Service;

import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;

public interface PostService {
    PostResponse.CreatePostResponse CreatePostService(PostRequest.CreatePostRequest request, String userId);
    PostResponse.GetPostsResponse GetPostsService(int page, int offset);
    PostResponse.GetPostResponse GetPostService(String postId);
    String PatchPostService(PostRequest.UpdatePostRequest request, String userId,String postId);
    String DeletePostService(String userId,String postId);
}
