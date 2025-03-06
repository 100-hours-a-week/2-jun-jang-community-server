package community.Api.User.Dtos;

import lombok.Getter;


public class UserRequest {
    @Getter
    public static class CreateUserRequest{
        private String email;
        private String password;
        private String nickname;
        private String profileImage;
    }
    @Getter
    public static class LoginUserRequest{
        private String email;
        private String password;
    }
    @Getter
    public static class UpdateUserProfileRequest {
        private String nickname;
        private String profileImage;
    }
    @Getter
    public static class UpdateUserPasswordRequest {
        private String password;

    }
}
