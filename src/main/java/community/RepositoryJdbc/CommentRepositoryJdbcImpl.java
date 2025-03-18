package community.RepositoryJdbc;

import community.Model.JdbcModel.CommentJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJdbcImpl implements CommentRepositoryJdbc {
    private final JdbcTemplate jdbcTemplate;


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


    @Override
    public void save(CommentJdbc comment) {
        String sql = "INSERT INTO comment (comment_id, content, post_id, user_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, comment.getCommentId(), comment.getContent(), comment.getPostId(),
                comment.getUserId(), OffsetDateTime.now(), OffsetDateTime.now());
    }


    @Override
    public List<CommentJdbc> findAllByPostId(String postId, int page, int offset){
        String sql = "SELECT * FROM comment WHERE post_id = ? ORDER BY created_at ASC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, commentJdbcRowMapper, postId, offset, (page - 1) * offset);
    }

    @Override
    public Optional<CommentJdbc> findByCommentId(String commentId) {
        String sql = "SELECT * FROM comment WHERE comment_id = ?";
        List<CommentJdbc> comments = jdbcTemplate.query(sql, commentJdbcRowMapper, commentId);
        return comments.stream().findFirst();
    }

    @Override
    public void deleteByCommentId(String commentId) {
        String sql = "DELETE FROM comment WHERE comment_id = ?";
        jdbcTemplate.update(sql, commentId);
    }

    @Override
    public void updateComment(String commentId, String content) {
        String sql = "UPDATE comment SET content = ?, updated_at = ? WHERE comment_id = ?";
        jdbcTemplate.update(sql, content, OffsetDateTime.now(), commentId);
    }
}
