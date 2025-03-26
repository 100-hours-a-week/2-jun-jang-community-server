package community.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_post_like") // 테이블명 지정
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPostLike extends BaseTime {

    @Id
    @Column(name = "like_id", nullable = false, unique = true)
    private String likeId; // 좋아요 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    public void changeIsLike() {
        this.isLike = !this.isLike;
    }
}
