package community.Api.User.Service;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;

public interface UserService {
    UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request);
}
