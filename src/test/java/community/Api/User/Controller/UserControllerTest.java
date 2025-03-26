package community.Api.User.Controller;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Api.User.Service.UserService;
import community.Common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    public void signUpTestSuccess() {
        //given
        UserRequest.CreateUserRequest createUserRequest = new UserRequest.CreateUserRequest();
        UserResponse.CreateUserResponse response = UserResponse.CreateUserResponse.builder()
                .userId("USER-123")
                .build();

        createUserRequest.setEmail("test@gmail.com");
        createUserRequest.setPassword("Test@1234");
        createUserRequest.setNickname("Test");
        createUserRequest.setProfileImage("https://i.ibb.co/JRCHphGm/2b502332-666e-4c66-98a2-9e910ff5b724.jpg");

        when(userService.createUserService(any(UserRequest.CreateUserRequest.class)))
                .thenReturn(response);
        //When
        ApiResponse<UserResponse.CreateUserResponse> result = userController.createUserController(createUserRequest);
        //Then
        assertEquals(true, result.isSuccess());
        verify(userService, times(1)).createUserService(createUserRequest);
    }
}
