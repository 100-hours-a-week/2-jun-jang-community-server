package community.Repository.RepositoryJpa;

import community.Model.JpaModel.PostJpa;
import community.Model.JpaModel.UserJpa;
import community.Model.JpaModel.UserPostLikeJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPostLikeRepositoryJpa extends JpaRepository<UserPostLikeJpa, String> {
    @Query("SELECT upl.isLike FROM UserPostLikeJpa upl WHERE upl.post.postId = :postId AND upl.user.userId = :userId")
    Optional<Boolean> findIsLikeByPostAndUser(@Param("postId") String postId, @Param("userId") String userId);


    Optional<UserPostLikeJpa> findByPostAndUser(PostJpa post, UserJpa user);
}
