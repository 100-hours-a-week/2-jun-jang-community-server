package community.RepositoryJdbc;

import community.Model.JdbcModel.UserPostLikeJdbc;

import java.util.Optional;

public interface UserPostLikeRepositoryJdbc {
    Optional<UserPostLikeJdbc> findByPostAndUser(String postId, String userId); // 특정 게시물의 좋아요 조회
    void save(UserPostLikeJdbc userPostLike); // 좋아요 생성
    void toggleLike(String postId, String userId); // 좋아요 토글 (ON/OFF)
}