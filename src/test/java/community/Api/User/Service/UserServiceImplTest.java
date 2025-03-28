package community.Api.User.Service;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Common.Enums.MessageCode;
import community.Exception.UserException.UserException;
import community.Model.User;
import community.Repository.UserRepository;
import community.Util.EntityValidator;
import community.Util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EntityValidator entityValidator;
    @InjectMocks
    private UserServiceImpl userService;

    private User createMockUser() {
        User user = new User();
        user.setUserId("USER-001");
        user.setEmail("test@example.com");
        user.setPassword("encoded-pw");
        user.setNickname("tester");
        user.setUserProfile("profile.png");
        return user;
    }

    @Test
    @DisplayName("회원가입 성공")
    void createUser_success() {
        UserRequest.CreateUserRequest request = UserRequest.CreateUserRequest.builder()
                .email("test@example.com")
                .password("plain-password")
                .nickname("tester")
                .profileImage("profile.png")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        
        UserResponse.CreateUserResponse response = userService.createUserService(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).startsWith("USER-");
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void createUser_duplicateEmail_fail() {
        UserRequest.CreateUserRequest request = UserRequest.CreateUserRequest.builder()
                .email("test@example.com")
                .password("pw")
                .nickname("nick")
                .profileImage("img.png")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(createMockUser()));

        assertThatThrownBy(() -> userService.createUserService(request))
                .isInstanceOf(UserException.UserIsValidException.class);
    }

    @Test
    @DisplayName("로그인 성공")
    void loginUser_success() {
        UserRequest.LoginUserRequest request = UserRequest.LoginUserRequest.builder()
                .email("test@example.com")
                .password("plain-password")
                .build();

        User mockUser = createMockUser();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("plain-password", "encoded-pw")).thenReturn(true);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.generateAccessToken("USER-001")).thenReturn("mock-access-token");
            jwtUtilMock.when(() -> JwtUtil.generateRefreshToken("USER-001")).thenReturn("mock-refresh-token");

            UserResponse.LoginUserResponse response = userService.loginUserService(request);

            assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token");
        }
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void loginUser_emailNotFound_fail() {
        UserRequest.LoginUserRequest request = UserRequest.LoginUserRequest.builder()
                .email("no-user@example.com")
                .password("pw")
                .build();

        when(userRepository.findByEmail("no-user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loginUserService(request))
                .isInstanceOf(UserException.LoginFailedException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginUser_wrongPassword_fail() {
        UserRequest.LoginUserRequest request = UserRequest.LoginUserRequest.builder()
                .email("test@example.com")
                .password("wrong-pw")
                .build();

        User mockUser = createMockUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong-pw", "encoded-pw")).thenReturn(false);

        assertThatThrownBy(() -> userService.loginUserService(request))
                .isInstanceOf(UserException.LoginFailedException.class);
    }

    @Test
    @DisplayName("유저 조회 성공")
    void getUser_success() {
        // given
        User mockUser = createMockUser();
        when(entityValidator.getValidUserOrThrow("USER-001")).thenReturn(mockUser);

        // when
        UserResponse.GetUserResponse response = userService.getUserService("USER-001");

        // then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("프로필 수정 - 닉네임과 프로필 모두 변경")
    void patchUserProfile_success() {
        // given
        User mockUser = createMockUser();
        when(entityValidator.getValidUserOrThrow("USER-001")).thenReturn(mockUser);

        UserRequest.UpdateUserProfileRequest request = UserRequest.UpdateUserProfileRequest.builder()
                .nickname("newNick")
                .profileImage("newImage.png")
                .build();
        ;

        // when
        String result = userService.patchUserProfileService("USER-001", request);

        // then
        assertThat(mockUser.getNickname()).isEqualTo("newNick");
        assertThat(mockUser.getUserProfile()).isEqualTo("newImage.png");
        assertThat(result).isEqualTo(MessageCode.PROFILE_UPDATED.getMessage());
    }

    @Test
    @DisplayName("프로필 수정 - 일부 필드가 null일 경우 기존 값 유지")
    void patchUserProfile_partialUpdate() {
        // given
        User mockUser = createMockUser();
        when(entityValidator.getValidUserOrThrow("USER-001")).thenReturn(mockUser);

        UserRequest.UpdateUserProfileRequest request = UserRequest.UpdateUserProfileRequest.builder()
                .nickname(null)
                .profileImage("updated.png")
                .build();


        // when
        String result = userService.patchUserProfileService("USER-001", request);

        // then
        assertThat(mockUser.getNickname()).isEqualTo("tester"); // 기존 값 유지
        assertThat(mockUser.getUserProfile()).isEqualTo("updated.png");
        assertThat(result).isEqualTo(MessageCode.PROFILE_UPDATED.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void putUserPassword_success() {
        // given
        User mockUser = createMockUser();
        when(entityValidator.getValidUserOrThrow("USER-001")).thenReturn(mockUser);

        UserRequest.UpdateUserPasswordRequest request = UserRequest.UpdateUserPasswordRequest.builder()
                .password("newPassword")
                .build();


        when(passwordEncoder.encode("newPassword")).thenReturn("encrypted-newPassword");

        // when
        String result = userService.putUserPasswordService("USER-001", request);

        // then
        assertThat(mockUser.getPassword()).isEqualTo("encrypted-newPassword");
        assertThat(result).isEqualTo(MessageCode.PASSWORD_UPDATED.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_success() {
        // given
        User mockUser = createMockUser();
        when(entityValidator.getValidUserOrThrow("USER-001")).thenReturn(mockUser);

        // when
        String result = userService.deleteUserService("USER-001");

        // then
        assertThat(mockUser.getDeletedAt()).isNotNull();
        assertThat(result).isEqualTo(MessageCode.USER_DELETED.getMessage());
    }
}
