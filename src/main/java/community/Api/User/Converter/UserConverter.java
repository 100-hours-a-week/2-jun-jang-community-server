package community.Api.User.Converter;

import community.Api.User.Dtos.UserResponse;

import community.Model.JpaModel.UserJpa;

public class UserConverter {

    public static UserResponse.CreateUserResponse toCreateUserResponse(UserJpa user) {
        return UserResponse.CreateUserResponse.builder()
                .userId(user.getUserId())
                .build();
    }
    public static UserResponse.LoginUserResponse toLoginUserResponse(String accessToken, String refreshToken) {
        return UserResponse.LoginUserResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    public static UserResponse.GetUserResponse toGetUserResponse(UserJpa user) {
        return UserResponse.GetUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getUserProfile())
                .build();
    }
}
