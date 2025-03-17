package community.RepositoryJdbc;

import community.Model.JdbcModel.UserPostLikeJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPostLikeRepositoryJdbcImpl implements UserPostLikeRepositoryJdbc {
    private final JdbcTemplate jdbcTemplate;


    private final RowMapper<UserPostLikeJdbc> userPostLikeRowMapper = (rs, rowNum) -> {
        UserPostLikeJdbc like = new UserPostLikeJdbc(
                rs.getString("like_id"),
                rs.getBoolean("is_like"),
                rs.getString("post_id"),
                rs.getString("user_id")
        );
        like.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        like.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return like;
    };


    @Override
    public Optional<UserPostLikeJdbc> findByPostAndUser(String postId, String userId) {
        String sql = "SELECT * FROM user_post_like WHERE post_id = ? AND user_id = ?";
        List<UserPostLikeJdbc> likes = jdbcTemplate.query(sql, userPostLikeRowMapper, postId, userId);
        return likes.stream().findFirst();
    }


    @Override
    public void save(UserPostLikeJdbc userPostLike) {
        String sql = "INSERT INTO user_post_like (like_id, is_like, post_id, user_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userPostLike.getLikeId(), userPostLike.isLike(), userPostLike.getPostId(),
                userPostLike.getUserId(), OffsetDateTime.now(), OffsetDateTime.now());
    }


    @Override
    public void toggleLike(String postId, String userId) {
        Optional<UserPostLikeJdbc> existingLike = findByPostAndUser(postId, userId);

        if (existingLike.isPresent()) {

            boolean newLikeStatus = !existingLike.get().isLike();
            String sql = "UPDATE user_post_like SET is_like = ?, updated_at = ? WHERE post_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, newLikeStatus, OffsetDateTime.now(), postId, userId);
        } else {

            save(new UserPostLikeJdbc(UUID.randomUUID().toString(), true, postId, userId));
        }
    }
}
