package community.Api.Post.Service;

import community.Model.Post;
import community.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitServiceTest {
    @InjectMocks
    private VisitService visitService;

    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .userId("USER-123")
                .nickname("tester")
                .userProfile("profile.png")
                .build();

        post = Post.builder()
                .postId("POST-123")
                .title("old title")
                .content("old content")
                .contentImage("old.png")
                .visitCount(0)
                .user(user)
                .build();
    }

    @Test
    @DisplayName("조회수 증가 테스트")
    void visitCount_success() {
        //given
        int increaseCount = 1;
        int oldCount = post.getVisitCount();
        //when
        visitService.increaseVisitCount(post);

        //then

        assertThat(post.getVisitCount()).isEqualTo(oldCount + increaseCount);


    }
}
