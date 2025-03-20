package community.Repository.RepositoryJpa;

import community.Model.JpaModel.CommentJpa;
import community.Model.JpaModel.PostJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepositoryJpa extends JpaRepository<CommentJpa, String> {
    Page<CommentJpa> findAllByPost(PostJpa post, Pageable pageable);
}
