package community.RepositoryJdbc;

import community.Model.JdbcModel.CommentJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJdbcImpl implements CommentRepositoryJdbc {
    private final JdbcTemplate jdbcTemplate;

    // ✅ RowMapper 정의
    private final RowMapper<CommentJdbc> commentJdbcRowMapper = (rs, rowNum) -> {
        CommentJdbc comment = new CommentJdbc(
                rs.getString("comment_id"),
                rs.getString("content"),
                rs.getString("post_id"),
                rs.getString("user_id")
        );
        comment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        comment.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return comment;
    };

    // 1️⃣ 댓글 생성
    @Override
    public void save(CommentJdbc comment) {
        String sql = "INSERT INTO comment (comment_id, content, post_id, user_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, comment.getCommentId(), comment.getContent(), comment.getPostId(),
                comment.getUserId(), OffsetDateTime.now(), OffsetDateTime.now());
    }

    // 2️⃣ 특정 게시물의 댓글 조회 (삭제되지 않은 댓글만)
    @Override
    public List<CommentJdbc> findByPostId(String postId) {
        String sql = "SELECT * FROM comment WHERE post_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, commentJdbcRowMapper, postId);
    }

    // 3️⃣ 댓글 삭제 (하드 삭제)
    @Override
    public void deleteByCommentId(String commentId) {
        String sql = "DELETE FROM comment WHERE comment_id = ?";
        jdbcTemplate.update(sql, commentId);
    }
}
