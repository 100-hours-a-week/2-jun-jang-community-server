package community.RepositoryJdbc;

import community.Model.JdbcModel.PostJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJdbcImpl implements PostRepositoryJdbc{
    private final JdbcTemplate jdbcTemplate;


    private final RowMapper<PostJdbc> postJdbcRowMapper = (rs, rowNum) -> {
        PostJdbc post = new PostJdbc(
                rs.getString("post_id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("content_img"),
                rs.getInt("like_count"),
                rs.getInt("visit_count"),
                rs.getInt("comment_count"),
                rs.getString("user_id"),
                rs.getObject("deleted_at", OffsetDateTime.class)
        );
        post.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        post.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return post;
    };
    @Override
    public void save(PostJdbc post) {
        String sql = "INSERT INTO post (post_id, title, content, content_img, like_count, visit_count, comment_count, user_id, created_at, updated_at, deleted_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getContentImage(),
                0, // 초기 좋아요 수
                0, // 초기 조회수
                0, // 초기 댓글 수
                post.getUserId(),
                OffsetDateTime.now(), // created_at
                OffsetDateTime.now(), // updated_at
                null // deleted_at (삭제되지 않음)
        );
    }

    @Override
    public List<String> findAllPostIds(int page, int offset) {
        String sql = "SELECT post_id FROM post WHERE deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.queryForList(sql, String.class, offset, (page - 1) * offset);
    }


    @Override
    public Optional<PostJdbc> findByPostId(String postId) {
        String sql = "SELECT * FROM post WHERE post_id = ? AND deleted_at IS NULL";
        List<PostJdbc> posts = jdbcTemplate.query(sql, postJdbcRowMapper, postId);
        return posts.stream().findFirst();
    }


    @Override
    public void updatePost(String postId, String title, String content, String contentImg) {
        String sql = "UPDATE post SET title = ?, content = ?, content_img = ?, updated_at = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, title, content, contentImg, OffsetDateTime.now(), postId);
    }


    @Override
    public void updateLikeCount(String postId, int likeCount) {
        String sql = "UPDATE post SET like_count = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, likeCount, postId);
    }


    @Override
    public void updateVisitCount(String postId, int visitCount) {
        String sql = "UPDATE post SET visit_count = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, visitCount, postId);
    }


    @Override
    public void updateCommentCount(String postId, int commentCount) {
        String sql = "UPDATE post SET comment_count = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, commentCount, postId);
    }


    @Override
    public void deletePost(String postId) {
        String sql = "UPDATE post SET deleted_at = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, OffsetDateTime.now(), postId);
    }
}
