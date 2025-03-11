package community.RepositoryJdbc;

import community.Model.JdbcModel.UserJdbc;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepositoryJdbc {
    void save(UserJdbc user);
    Optional<UserJdbc> findByUserId(String userId);
    Optional<UserJdbc> findByEmail(String email);
    void updateUserInfo(String userId, String nickname, String userProfile);
    void updatePassword(String userId, String newPassword);
    void deleteByUserId(String userId);
}
