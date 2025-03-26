package community.Api.Post.Dtos;

import lombok.Getter;

public class PostRequest {
    @Getter
    public static class CreatePostRequest {
        private String title;
        private String content;
        private String contentImage;
    }
    @Getter
    public static class PatchPostRequest {
        private String title;
        private String content;
        private String contentImage;
    }
    @Getter
    public static class CreateCommentRequest{
        private String content;
    }
    @Getter
    public static class PutCommentRequest {
        private String content;
    }

}
