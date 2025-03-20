package community.Repository.RepositoryJpa;

import community.Model.JpaModel.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryJpa extends JpaRepository<UserJpa, String> {
    Optional<UserJpa> findByEmail(String email);
}
