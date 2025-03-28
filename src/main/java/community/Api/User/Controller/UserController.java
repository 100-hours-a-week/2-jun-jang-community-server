package community.Api.User.Controller;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Api.User.Service.UserService;
import community.Common.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("Authentication is NULL in DELETE request");
        }

        log.info("Authenticated userId: {}", authentication.getName());
        return authentication.getName();
    }


    @PostMapping("")
    public ApiResponse<UserResponse.CreateUserResponse> createUserController(@RequestBody UserRequest.CreateUserRequest request) {

        return new ApiResponse<>(userService.createUserService(request), "201");
    }

    @PostMapping("/token")
    public ApiResponse<UserResponse.LoginUserResponse> loginUserController(@RequestBody UserRequest.LoginUserRequest request) {
        return new ApiResponse<>(userService.loginUserService(request), "200");
    }

    @GetMapping("/profile")
    public ApiResponse<UserResponse.GetUserResponse> getUserController() {

        return new ApiResponse<>(userService.getUserService(getAuthenticatedUserId()), "200");
    }

    @PatchMapping("/profile")
    public ApiResponse<String> patchUserProfileController(@RequestBody UserRequest.UpdateUserProfileRequest request) {

        return new ApiResponse<>(userService.patchUserProfileService(getAuthenticatedUserId(), request), "200");
    }

    @PutMapping("/password")
    public ApiResponse<String> putUserPasswordController(@RequestBody UserRequest.UpdateUserPasswordRequest request) {
        return new ApiResponse<>(userService.putUserPasswordService(getAuthenticatedUserId(), request), "200");
    }

    @Transactional
    @DeleteMapping("")
    public ApiResponse<String> deleteUserController() {
        return new ApiResponse<>(userService.deleteUserService(getAuthenticatedUserId()), "200");
    }

}
