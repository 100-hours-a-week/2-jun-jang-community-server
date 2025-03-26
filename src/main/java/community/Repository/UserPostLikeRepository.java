package community.Repository;

import community.Model.Post;
import community.Model.User;
import community.Model.UserPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPostLikeRepository extends JpaRepository<UserPostLike, String> {
    @Query("SELECT upl.isLike FROM UserPostLike upl WHERE upl.post.postId = :postId AND upl.user.userId = :userId")
    Optional<Boolean> findIsLikeByPostAndUser(@Param("postId") String postId, @Param("userId") String userId);


    Optional<UserPostLike> findByPostAndUser(Post post, User user);
}
