package community.Api.Image.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ImageResponse {
    @Getter
    @Setter
    @Builder
    public static class ImageUploadDto {
        private String fileName;
        private String fileUrl;
    }

    @Getter
    @NoArgsConstructor
    public static class ImgBBImage {
        private String filename;
        private String name;
        private String mime;
        private String extension;
        private String url;
    }

    @Getter
    @NoArgsConstructor
    public static class ImgBBData {
        private String id;
        private String title;
        private String urlViewer;
        private String url;
        private String displayUrl;
        private int width;
        private int height;
        private int size;
        private long time;
        private int expiration;
        private ImgBBImage image;
        private ImgBBImage thumb;
        private ImgBBImage medium;
        private String deleteUrl;
    }

    @Getter
    @NoArgsConstructor
    public static class ImgBBInfo {
        private ImgBBData data;
        private boolean success;
        private int status;
    }
}
