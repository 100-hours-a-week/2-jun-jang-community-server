package community.Api.Post.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PostRequest {
    @Getter
    public static class CreatePostRequest {
        private String title;
        private String content;
        private String contentImage;
    }
    @Getter
    public static class UpdatePostRequest {
        private String title;
        private String content;
        private String contentImage;
    }
    @Getter
    public static class CreateCommentRequest{
        private String content;
    }
    @Getter
    public static class UpdateCommentRequest {
        private String content;
    }

}
