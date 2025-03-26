package community.Api.User.Dtos;

import lombok.Getter;
import lombok.Setter;


public class UserRequest {
    @Getter
    @Setter
    public static class CreateUserRequest {
        private String email;
        private String password;
        private String nickname;
        private String profileImage;
    }

    @Getter
    @Setter
    public static class LoginUserRequest {
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
