package community.Api.User.Service;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Exception.UserException.UserException;
import community.Model.User;
import community.Repository.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("회원가입 성공")
    void createUser_success() {
        // given
        UserRequest.CreateUserRequest request = new UserRequest.CreateUserRequest();
        request.setEmail("test234@example.com");
        request.setPassword("Test@1234");
        request.setNickname("tester");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pw");

        // when
        UserResponse.CreateUserResponse response = userService.createUserService(request);

        // then
        assertThat(response.getUserId()).startsWith("USER-");

        System.out.println("회원가입 성공! " + response.getUserId());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String rawPassword = "Test@1234";
        String encodedPassword = "encoded-pw";

        UserRequest.LoginUserRequest request = new UserRequest.LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword(rawPassword);

        User mockUser = new User();
        mockUser.setUserId("USER-001");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword(encodedPassword);
        mockUser.setNickname("tester");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        try (MockedStatic<JwtUtil> mockedJwt = mockStatic(JwtUtil.class)) {
            mockedJwt.when(() -> JwtUtil.generateAccessToken("USER-001"))
                    .thenReturn("access-token");
            mockedJwt.when(() -> JwtUtil.generateRefreshToken("USER-001"))
                    .thenReturn("refresh-token");

            // when
            UserResponse.LoginUserResponse response = userService.loginUserService(request);

            // then
            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

            System.out.println("로그인 성공 : 엑세스 토큰 : " + response.getAccessToken());
        }
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fall_wrongPassword() {
        UserRequest.LoginUserRequest request = new UserRequest.LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test@1234");

        User mockUser = new User();
        mockUser.setUserId("USER-001");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("Test@1234");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(mockUser));
        //패스워드 매칭은 해주지만, 어차파 false리턴
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.loginUserService(request))
                .isInstanceOf(UserException.LoginFailedException.class)
                .hasMessageContaining("이메일 또는 비밀번호가 잘못되었습니다.");

        System.out.println("로그인 실패 - 비밀번호 불일치");
    }

}
