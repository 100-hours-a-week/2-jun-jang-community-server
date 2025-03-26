package community.Api.User.Dtos;

import lombok.Builder;
import lombok.Getter;


public class UserResponse {
    @Getter
    @Builder
    public static class CreateUserResponse {
        private String userId;
    }

    @Getter
    @Builder
    public static class LoginUserResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class GetUserResponse {
        private String userId;
        private String email;
        private String nickname;
        private String profileImage;
    }
}
