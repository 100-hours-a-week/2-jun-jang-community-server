package community.Api.Image.Service;

import community.Api.Image.Dtos.ImageResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageResponse.ImageUploadDto UploadImage(@RequestParam MultipartFile file);
}
