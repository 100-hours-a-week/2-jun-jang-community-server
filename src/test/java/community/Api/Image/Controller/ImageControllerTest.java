package community.Api.Image.Controller;

import community.Api.Image.Dtos.ImageResponse;
import community.Api.Image.Service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    @DisplayName("이미지 업로드 성공")
    void imageUpload_success() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", MediaType.IMAGE_PNG_VALUE, "dummy-image-content".getBytes());

        ImageResponse.ImageUploadDto response = ImageResponse.ImageUploadDto.builder().
                fileUrl("https://example.com/image/test.png")
                .fileName(file.getOriginalFilename())
                .build();

        when(imageService.UploadImage(Mockito.any())).thenReturn(response);

        // when & then
        mockMvc.perform(multipart("/images")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.data.fileUrl").value("https://example.com/image/test.png"));
    }
}
