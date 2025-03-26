package community.Api.User.Service;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;

public interface UserService {
    UserResponse.CreateUserResponse createUserService(UserRequest.CreateUserRequest request);

    UserResponse.LoginUserResponse loginUserService(UserRequest.LoginUserRequest request);

    UserResponse.GetUserResponse getUserService(String userId);

    String patchUserProfileService(String userId, UserRequest.UpdateUserProfileRequest request);

    String putUserPasswordService(String userId, UserRequest.UpdateUserPasswordRequest request);

    String deleteUserService(String userId);
}
