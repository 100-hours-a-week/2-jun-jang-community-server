package community.Api.User.Converter;

import community.Api.User.Dtos.UserResponse;
import community.Model.JdbcModel.UserJdbc;

public class UserConverter {

    public static UserResponse.CreateUserResponse toCreateUserResponse(UserJdbc user) {
        return UserResponse.CreateUserResponse.builder()
                .userId(user.getUserId())
                .build();
    }
}
