package community.Api.Image.Controller;

import community.Api.Image.Dtos.ImageResponse;
import community.Api.Image.Service.ImageService;
import community.Common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @PostMapping("")
    public ApiResponse<ImageResponse.ImageUploadDto> ImageUploadController(@RequestParam MultipartFile file) throws IOException {
        return new ApiResponse<>(imageService.UploadImage(file), "201");

    }
}
