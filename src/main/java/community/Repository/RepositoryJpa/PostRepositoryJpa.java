package community.Repository.RepositoryJpa;

import community.Model.JpaModel.PostJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PostRepositoryJpa extends JpaRepository<PostJpa, String> {
    Page<PostJpa> findAll(Pageable pageable);
}
