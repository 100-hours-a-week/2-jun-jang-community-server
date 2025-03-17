package community.Model.JdbcModel;

import lombok.*;

import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
@Getter
@Setter
public class UserJdbc extends BaseTimeJdbc {
    private String userId;
    private String nickname;
    private String email;
    private String password;
    private String userProfile;
    private OffsetDateTime deletedAt;
    public UserJdbc(String userId, String nickname, String email, String password, String userProfile, OffsetDateTime deletedAt) {
        super();
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.userProfile = userProfile;
        this.deletedAt = deletedAt;
    }


}
