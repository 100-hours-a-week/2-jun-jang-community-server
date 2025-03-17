package community.Api.User.Controller;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Api.User.Service.UserService;
import community.Common.ApiResponse;
import community.utill.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    //더미 값만 생성
    @PostMapping("")
    public ApiResponse<UserResponse.CreateUserResponse> CreateUserController(@RequestBody UserRequest.CreateUserRequest request){

        return new ApiResponse<>(userService.CreateUserService(request),"201");
    }
    @PostMapping("/token")
    public ApiResponse<UserResponse.LoginUserResponse> LoginUserController(@RequestBody UserRequest.LoginUserRequest request){
        return new ApiResponse<>(userService.LoginUserService(request),"200");
    }
    @GetMapping("/profile")
    public ApiResponse<UserResponse.GetUserResponse> GetUserController(){

        return new ApiResponse<>(userService.GetUserService(getAuthenticatedUserId()),"200");
    }
    @PatchMapping("/profile")
    public ApiResponse<String>PatchUserProfileController(@RequestBody UserRequest.UpdateUserProfileRequest request){

        return new ApiResponse<>(userService.PatchUserProfileService(getAuthenticatedUserId(),request),"200");
    }
    @PutMapping("/password")
    public ApiResponse<String>PutUserPasswordController(@RequestBody UserRequest.UpdateUserPasswordRequest request){
        return new ApiResponse<>(userService.PutUserPasswordService(getAuthenticatedUserId(),request),"200");
    }
    @Transactional
    @DeleteMapping("")
    public ApiResponse<String>DeleteUserController(){
        return new ApiResponse<>(userService.DeleteUserService(getAuthenticatedUserId()),"200");
    }

}
