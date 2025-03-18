package community.Api.Post.Service;

import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;

public interface PostService {
    PostResponse.CreatePostResponse CreatePostService(PostRequest.CreatePostRequest request, String userId);
    PostResponse.GetPostsResponse GetPostsService(int page, int offset);
    PostResponse.GetPostResponse GetPostService(String postId,String userId);
    String PatchPostService(PostRequest.PatchPostRequest request, String userId, String postId);
    String DeletePostService(String userId,String postId);

    PostResponse.CreateCommentResponse CreateCommentService(PostRequest.CreateCommentRequest request, String userId,String postId);
    PostResponse.GetCommentsResponse GetCommentsService(String postId,int page, int offset);
    String PutCommentService(String postId, PostRequest.PutCommentRequest request, String userId,String commentId);
    String DeleteCommentService(String postId, String commentId, String userId);
    String DoLikeService(String postId, String userId);
}
