package community.Api.Image.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import community.Api.Image.Dtos.ImageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // S3 버킷 이름 설정
        imageService.bucket = "mock-bucket";
    }

    @Test
    @DisplayName("이미지 업로드 성공")
    void uploadImage_success() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "fake-image-content".getBytes());

        // S3 URL mock
        URL mockUrl = new URL("https://mock-bucket.s3.amazonaws.com/img/fake-image.jpg");

        // amazonS3Client mock
        when(amazonS3Client.getUrl(anyString(), anyString())).thenReturn(mockUrl);

        // when
        ImageResponse.ImageUploadDto response = imageService.UploadImage(multipartFile);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getFileUrl()).isEqualTo(mockUrl.toString());
        assertThat(response.getFileName()).startsWith("img/");
        assertThat(response.getFileName()).endsWith(".jpg");

        // verify that S3 upload method was called
        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("확장자 없는 파일 업로드 시 기본 확장자 사용")
    void uploadImage_withoutExtension_usesDefaultExtension() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "filename", "image/jpeg", "no-extension-content".getBytes());

        URL mockUrl = new URL("https://mock-bucket.s3.amazonaws.com/img/default.jpg");

        when(amazonS3Client.getUrl(anyString(), anyString())).thenReturn(mockUrl);

        // when
        ImageResponse.ImageUploadDto response = imageService.UploadImage(multipartFile);

        // then
        assertThat(response.getFileName()).endsWith(".jpg");
    }
}
