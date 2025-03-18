package community.Api.Post.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PostResponse {
    @Setter
    @Getter
    @Builder
    public static class CreatePostResponse{
        private String postId;
    }
    @Setter
    @Getter
    @Builder
    public static class PostsItem{
        private String postId;
        private String title;
        private int likeCount;
        private int commentCount;
        private int visitCount;
        private String userId;
        private String userName;
        private String createdAt;
        private String userProfileImage;
    }
    @Setter
    @Getter
    @Builder
    public static class GetPostsResponse {
        private List<PostsItem> posts;
    }
    @Setter
    @Getter
    @Builder
    public static class GetPostResponse {
        private String postId;
        private String title;
        private String content;
        private String userId;
        private String userName;
        private String userProfileImage;
        private String contentImage;
        private String createdAt;
        private int likeCount;
        private int commentCount;
        private int visitCount;
        private Boolean isLike;
    }
    @Setter
    @Getter
    @Builder
    public static class CreateCommentResponse {
        private String postId;
        private String commentId;
    }
    @Setter
    @Builder
    @Getter
    public static class CommentItem{
        private String commentId;
        private String userId;
        private String userName;
        private String userProfileImage;
        private String createdAt;
        private String content;
    }
    @Setter
    @Getter
    @Builder
    public static class GetCommentsResponse {
        private List<CommentItem> comments;
    }
}
