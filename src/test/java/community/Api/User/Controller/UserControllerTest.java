package community.Api.User.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Api.User.Service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 - HTTP 요청/응답 흐름 테스트")
    void signUp_success() throws Exception {
        // given
        UserRequest.CreateUserRequest request = UserRequest.CreateUserRequest.builder()
                .email("test@gmail.com")
                .password("test@1234")
                .nickname("Test")
                .profileImage("image.jpg").build();

        UserResponse.CreateUserResponse response = UserResponse.CreateUserResponse.builder()
                .userId("USER-123")
                .build();

        when(userService.createUserService(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // ApiResponse가 "201"을 반환하므로, 상태도 맞추는 게 좋음
                .andExpect(jsonPath("$.data.userId").value("USER-123"))
                .andExpect(jsonPath("$.success").value(true));

        verify(userService, times(1)).createUserService(any());
    }

    @Test
    @DisplayName("로그인 성공 - HTTP 요청/응답 흐름 테스트")
    void login_success() throws Exception {

        // given
        UserRequest.LoginUserRequest request = UserRequest.LoginUserRequest.builder()
                .email("test@gmail.com")
                .password("Test@1234")
                .build();


        UserResponse.LoginUserResponse response = UserResponse.LoginUserResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(userService.loginUserService(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/users/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getUserProfile_success() throws Exception {
        // given
        String userId = "USER-123";
        mockSecurityContext(userId);
        UserResponse.GetUserResponse response = UserResponse.GetUserResponse.builder()
                .userId(userId)
                .nickname("TestUser")
                .email("test@gmail.com")
                .profileImage("img.jpg")
                .build();

        when(userService.getUserService(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/users/profile")
                        .principal(() -> userId)) // SecurityContext mocking
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void patchUserProfile_success() throws Exception {
        // given
        String userId = "USER-123";
        mockSecurityContext(userId);
        UserRequest.UpdateUserProfileRequest request = UserRequest.UpdateUserProfileRequest.builder()
                .nickname("NewName")
                .profileImage("new-image.jpg")
                .build();


        when(userService.patchUserProfileService(eq(userId), any())).thenReturn("프로필 수정 완료");

        // when & then
        mockMvc.perform(patch("/users/profile")
                        .principal(() -> userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("프로필 수정 완료"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    void putPassword_success() throws Exception {
        // given
        String userId = "USER-123";
        mockSecurityContext(userId);
        UserRequest.UpdateUserPasswordRequest request = UserRequest.UpdateUserPasswordRequest.builder()
                .password("NewPassword123!")
                .build();


        when(userService.putUserPasswordService(eq(userId), any())).thenReturn("비밀번호 수정 완료");

        mockMvc.perform(put("/users/password")
                        .principal(() -> userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("비밀번호 수정 완료"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void deleteUser_success() throws Exception {
        // given
        String userId = "USER-123";
        mockSecurityContext(userId);
        when(userService.deleteUserService(userId)).thenReturn("회원탈퇴 완료");

        mockMvc.perform(delete("/users")
                        .principal(() -> userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("회원탈퇴 완료"))
                .andExpect(jsonPath("$.success").value(true));
    }

    private void mockSecurityContext(String userId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userId);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }
}

