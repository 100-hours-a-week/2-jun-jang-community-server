package community.Api.Post.Repository;

import community.Model.Post;
import community.Model.User;
import community.Repository.PostRepository;
import community.Repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("게시글 전체 조회 - 유저 정보 포함")
    void findAll_withUserEntityGraph() {
        // given
        User user = User.builder()
                .userId("USER-" + UUID.randomUUID())
                .email("test@example.com")
                .nickname("tester")
                .userProfile("profile.png")
                .password("password")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .postId("POST-" + UUID.randomUUID())
                .title("제목")
                .content("내용")
                .user(user)
                .build();
        postRepository.save(post);

        // when
        Page<Post> postPage = postRepository.findAll(PageRequest.of(0, 10));

        // then
        assertThat(postPage.getContent()).hasSize(1);
        assertThat(postPage.getContent().get(0).getUser().getNickname()).isEqualTo("tester");
    }
}
