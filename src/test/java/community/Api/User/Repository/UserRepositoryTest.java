package community.Api.User.Repository;

import community.Model.User;
import community.Repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void findByEmail_success() {
        // given
        User user = User.builder()
                .userId("USER-001")
                .email("test@example.com")
                .password("hashed-password")
                .nickname("Tester")
                .userProfile("image.jpg")
                .build();

        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("USER-001");
        assertThat(result.get().getNickname()).isEqualTo("Tester");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 실패 (존재하지 않음)")
    void findByEmail_fail() {
        // when
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(result).isEmpty();
    }
}
