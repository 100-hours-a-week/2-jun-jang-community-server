package community.RepositoryJdbc;

import community.Model.JdbcModel.CommentJdbc;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryJdbc {
    void save(CommentJdbc comment); // 1. 댓글 생성
    List<CommentJdbc> findAllByPostId(String postId, int page, int offset);
    Optional<CommentJdbc> findByCommentId(String commentId);
    // 2. 특정 게시물의 댓글 조회
    void deleteByCommentId(String commentId);
    void updateComment(String commentId, String content);
}
