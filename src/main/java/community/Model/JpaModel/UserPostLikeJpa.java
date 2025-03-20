package community.Model.JpaModel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_post_like") // 테이블명 지정
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPostLikeJpa extends BaseTimeJpa {

    @Id
    @Column(name = "like_id", nullable = false, unique = true)
    private String likeId; // 좋아요 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostJpa post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpa user;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;
}
