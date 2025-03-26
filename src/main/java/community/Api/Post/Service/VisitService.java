package community.Api.Post.Service;

import community.Model.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//트랜잭션 분리용이라 interface로 추상화 X
@Service
public class VisitService {
    @Transactional
    public void increaseVisitCount(Post post) {
        post.increaseVisitCount();
    }
}
