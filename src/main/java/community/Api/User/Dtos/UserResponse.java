package community.Api.User.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class UserResponse {
    @Getter
    @Setter
    @Builder
    public static class CreateUserResponse{
        private String userId;
    }
    @Getter
    @Setter
    @Builder
    public static class LoginUserResponse{
        private String accessToken;
        private String refreshToken;
    }
    @Getter
    @Setter
    @Builder
    public static class GetUserResponse{
        private String email;
        private String nickname;
        private String profileImage;
    }
}
