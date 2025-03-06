package community.Api.User.Controller;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Common.ApiResponse;
import community.utill.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private JwtUtil jwtUtill;
    //더미 값만 생성
    @PostMapping("")
    public ApiResponse<UserResponse.CreateUserResponse> CreateUserController(@RequestBody UserRequest.CreateUserRequest request){

        return new ApiResponse<>(UserResponse.CreateUserResponse.builder().userId("USER-"+ UUID.randomUUID().toString()).build(),"201");
    }
    @PostMapping("/token")
    public ApiResponse<UserResponse.LoginUserResponse> LoginUserController(@RequestBody UserRequest.LoginUserRequest request){
        return new ApiResponse<>(UserResponse.LoginUserResponse.builder()
                .accessToken(jwtUtill.generateAccessToken("User-xxxx"))
                .refreshToken(jwtUtill.generateRefreshToken("USER-xxxx")).build(),"200");
    }
    @GetMapping("/profile")
    public ApiResponse<UserResponse.GetUserResponse> GetUserController(){
        return new ApiResponse<>(UserResponse.GetUserResponse.builder()
                .email("test@email.com")
                .nickname("준")
                .profileImage("https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2Fcf024025-486d-4514-84ae-3a7c5951c17c%2F2a13714f-0a1e-4954-bebe-1d8e95db1e8c%2Fimage.png?table=block&id=5dc293f8-0db6-423d-8e2e-037a1764f96b&spaceId=cf024025-486d-4514-84ae-3a7c5951c17c&width=2000&userId=32221845-7314-4f9b-860f-b6cd2cfebc4f&cache=v2")
                .build(),"200");
    }
    @PatchMapping("/profile")
    public ApiResponse<String>PatchUserProfileController(@RequestBody UserRequest.UpdateUserProfileRequest request){
        return new ApiResponse<>("프로필 변경에 성공했습니다.","200");
    }
    @PutMapping("/password")
    public ApiResponse<String>PutUserPasswordController(@RequestBody UserRequest.UpdateUserPasswordRequest request){
        return new ApiResponse<>("비밀번호 변경에 성공했습니다.","200");
    }
    @DeleteMapping("")
    public ApiResponse<String>DeleteUserController(){
        return new ApiResponse<>("탈퇴가 완료되었습니다.","200");
    }

}
