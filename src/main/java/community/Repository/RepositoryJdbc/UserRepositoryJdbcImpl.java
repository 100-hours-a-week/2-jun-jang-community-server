//package Repository.RepositoryJdbc;
//
//import community.Model.JdbcModel.UserJdbc;
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//import java.util.Optional;
//@Repository
//@RequiredArgsConstructor
//public class UserRepositoryJdbcImpl implements UserRepositoryJdbc{
//    private final JdbcTemplate jdbcTemplate;
//
//    private final RowMapper<UserJdbc> userJdbcRowMapper= (rs, rowNum) -> {
//        UserJdbc user= new UserJdbc(
//                rs.getString("user_id"),
//                rs.getString("nickname"),
//                rs.getString("email"),
//                rs.getString("password"),
//                rs.getString("user_profile"),
//                rs.getObject("deleted_at",java.time.OffsetDateTime.class)
//        );
//        user.setCreatedAt(rs.getObject("created_at", java.time.OffsetDateTime.class));
//        user.setUpdatedAt(rs.getObject("updated_at", java.time.OffsetDateTime.class));
//        return user;
//    };
//
//    @Override
//    public void save(UserJdbc user) {
//        String sql = "INSERT INTO user (user_id, nickname, email, password, user_profile, created_at, updated_at) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?)";
//        jdbcTemplate.update(sql, user.getUserId(), user.getNickname(), user.getEmail(), user.getPassword(),
//                user.getUserProfile(), user.getCreatedAt(), user.getUpdatedAt());
//
//    }
//    @Override
//    public Optional<UserJdbc> findByUserId(String userId) {
//        String sql = "SELECT * FROM user WHERE user_id = ? AND deleted_at IS NULL"; // ✅ 삭제되지 않은 유저만 조회
//        List<UserJdbc> users = jdbcTemplate.query(sql, userJdbcRowMapper, userId);
//        return users.stream().findFirst();
//    }
//
//    @Override
//    public Optional<UserJdbc> findByEmail(String email) {
//        String sql = "SELECT * FROM user WHERE email = ? AND deleted_at IS NULL"; // ✅ 삭제되지 않은 유저만 조회
//        List<UserJdbc> users = jdbcTemplate.query(sql, userJdbcRowMapper, email);
//        return users.stream().findFirst();
//    }
//    // 사용자 업데이트 (updatedAt만 갱신)
//    @Override
//    public void updateUserInfo(String userId, String nickname, String userProfile) {
//        String sql = "UPDATE user SET nickname = ?, user_profile = ?, updated_at = ? WHERE user_id = ?";
//        jdbcTemplate.update(sql, nickname, userProfile, OffsetDateTime.now(), userId);
//    }
//
//    @Override
//    public void updatePassword(String userId, String newPassword) {
//        String sql = "UPDATE user SET password = ?, updated_at = ? WHERE user_id = ?";
//        jdbcTemplate.update(sql, newPassword, OffsetDateTime.now(), userId);
//    }
//
//    // userId로 사용자 삭제
//    @Override
//    public void deleteByUserId(String userId) {
//        String sql = "UPDATE user SET deleted_at = ? WHERE user_id = ?";
//        jdbcTemplate.update(sql, OffsetDateTime.now(), userId);
//    }
//
//}
