package community.Api.Post.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.Api.Post.Dtos.PostRequest;
import community.Api.Post.Dtos.PostResponse;
import community.Api.Post.Service.PostService;
import community.Util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PostController.class)
@ExtendWith(SpringExtension.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_success() throws Exception {
        // given
        PostRequest.CreatePostRequest request = PostRequest.CreatePostRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        String fakeUserId = "USER-123";
        PostResponse.CreatePostResponse response = PostResponse.CreatePostResponse.builder()
                .postId("POST-001")
                .build();

        // JwtUtil static 모킹
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(JwtUtil::getAuthenticatedUserId).thenReturn(fakeUserId);
            when(postService.createPostService(any(), eq(fakeUserId))).thenReturn(response);

            // when & then
            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.postId").value("POST-001"))
                    .andExpect(jsonPath("$.code").value("201"));
        }
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void getPost_success() throws Exception {
        String fakeUserId = "USER-123";
        String postId = "POST-001";

        PostResponse.GetPostResponse response = PostResponse.GetPostResponse.builder()
                .postId(postId)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(JwtUtil::getAuthenticatedUserId).thenReturn(fakeUserId);
            when(postService.getPostService(postId, fakeUserId)).thenReturn(response);

            mockMvc.perform(get("/posts/{postId}", postId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.postId").value(postId))
                    .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                    .andExpect(jsonPath("$.code").value("200"));
        }
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getPosts_success() throws Exception {
        PostResponse.GetPostsResponse response = PostResponse.GetPostsResponse.builder()
                .posts(List.of())
                .build();

        when(postService.getPostsService(0, 20)).thenReturn(response);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts").isArray())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void patchPost_success() throws Exception {
        String fakeUserId = "USER-123";
        String postId = "POST-001";

        PostRequest.PatchPostRequest request = PostRequest.PatchPostRequest.builder()
                .title("수정 제목")
                .content("수정 내용")
                .build();

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(JwtUtil::getAuthenticatedUserId).thenReturn(fakeUserId);
            when(postService.patchPostService(any(), eq(fakeUserId), eq(postId))).thenReturn("게시글 수정 완료");

            mockMvc.perform(patch("/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("게시글 수정 완료"))
                    .andExpect(jsonPath("$.code").value("200"));
        }
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_success() throws Exception {
        String fakeUserId = "USER-123";
        String postId = "POST-001";

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(JwtUtil::getAuthenticatedUserId).thenReturn(fakeUserId);
            when(postService.deletePostService(postId, fakeUserId)).thenReturn("삭제 완료");

            mockMvc.perform(delete("/posts/{postId}", postId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("삭제 완료"))
                    .andExpect(jsonPath("$.code").value("200"));
        }
    }
}
