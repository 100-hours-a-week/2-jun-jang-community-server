package community.Api.User.Dtos;

import lombok.Builder;
import lombok.Getter;


public class UserRequest {
    @Getter
    @Builder
    public static class CreateUserRequest {
        private String email;
        private String password;
        private String nickname;
        private String profileImage;
    }

    @Getter
    @Builder
    public static class LoginUserRequest {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class UpdateUserProfileRequest {
        private String nickname;
        private String profileImage;
    }

    @Getter
    @Builder
    public static class UpdateUserPasswordRequest {
        private String password;

    }
}
