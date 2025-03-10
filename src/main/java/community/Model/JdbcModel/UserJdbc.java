package community.Model.JdbcModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJdbc extends BaseTimeJdbc{
    private String userId;
    private String nickname;
    private String email;
    private String password;
    private String userProfile;
    public UserJdbc(String userId, String nickname, String email, String password, String userProfile) {
        super();
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.userProfile = userProfile;
    }


}
