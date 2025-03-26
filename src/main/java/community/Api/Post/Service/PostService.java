package community.Api.Post.Service;

import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;

public interface PostService {
    PostResponse.CreatePostResponse createPostService(PostRequest.CreatePostRequest request, String userId);

    PostResponse.GetPostsResponse getPostsService(int page, int offset);

    PostResponse.GetPostResponse getPostService(String postId, String userId);

    String patchPostService(PostRequest.PatchPostRequest request, String userId, String postId);

    String deletePostService(String userId, String postId);

    PostResponse.CreateCommentResponse createCommentService(PostRequest.CreateCommentRequest request, String userId, String postId);

    PostResponse.GetCommentsResponse getCommentsService(String postId, int page, int offset);

    String putCommentService(String postId, PostRequest.PutCommentRequest request, String userId, String commentId);

    String deleteCommentService(String postId, String commentId, String userId);

    String doLikeService(String postId, String userId);
}
