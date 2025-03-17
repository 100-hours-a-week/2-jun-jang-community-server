package community.Api.User.Service;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;

public interface UserService {
    UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request);
    UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request);
    UserResponse.GetUserResponse GetUserService(String userId);
    String PatchUserProfileService(String userId,UserRequest.UpdateUserProfileRequest request);
    String PutUserPasswordService(String userId,UserRequest.UpdateUserPasswordRequest request);
    String DeleteUserService(String userId);
}
