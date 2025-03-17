package community.RepositoryJdbc;

import community.Model.JdbcModel.PostJdbc;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryJdbc {
    void save(PostJdbc post);
    List<String> findAllPostIds(int page, int offset);
    Optional<PostJdbc> findByPostId(String postId);
    void updatePost(String postId, String title, String content, String contentImg);
    void updateLikeCount(String postId, int likeCount);
    void updateVisitCount(String postId, int visitCount);
    void updateCommentCount(String postId, int commentCount);
    void deletePost(String postId);
}