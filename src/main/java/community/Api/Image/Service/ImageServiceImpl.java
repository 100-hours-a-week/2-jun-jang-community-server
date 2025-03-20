package community.Api.Image.Service;

import community.Api.Image.Dtos.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final WebClient webClient;
    @Value("${image.secret-key}")
    private String secretKey;
    public ImageResponse.ImageUploadDto UploadImage(MultipartFile file){
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", secretKey);
        try{
            body.add("image",new NamedByteArrayResource(file.getBytes(), UUID.randomUUID().toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String apiUrl = "https://api.imgbb.com/1/upload";
        ImageResponse.ImgBBData imgBBData= webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ImageResponse.ImgBBInfo.class)
                .blockOptional()
                .map(ImageResponse.ImgBBInfo::getData)
                .orElseThrow(() -> new RuntimeException());
        return ImageResponse.ImageUploadDto.builder()
                .fileUrl(imgBBData.getImage().getUrl())
                .fileName(imgBBData.getImage().getName()).build();

    }
    private static class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        public NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
