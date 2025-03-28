package community.Api.Post.Dtos;

import lombok.Builder;
import lombok.Getter;

public class PostRequest {
    @Getter
    @Builder
    public static class CreatePostRequest {
        private String title;
        private String content;
        private String contentImage;
    }

    @Getter
    @Builder
    public static class PatchPostRequest {
        private String title;
        private String content;
        private String contentImage;
    }

    @Getter
    @Builder
    public static class CreateCommentRequest {
        private String content;
    }

    @Getter
    @Builder
    public static class PutCommentRequest {
        private String content;
    }

}
