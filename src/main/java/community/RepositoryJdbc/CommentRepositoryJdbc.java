package community.RepositoryJdbc;

import community.Model.JdbcModel.CommentJdbc;

import java.util.List;

public interface CommentRepositoryJdbc {
    void save(CommentJdbc comment); // 1. 댓글 생성
    List<CommentJdbc> findByPostId(String postId); // 2. 특정 게시물의 댓글 조회
    void deleteByCommentId(String commentId);
}
