package community.Model.JpaModel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment") // 테이블명 지정
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentJpa extends BaseTimeJpa {

    @Id
    @Column(name = "comment_id", nullable = false, unique = true)
    private String commentId; // 댓글 ID

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostJpa post; // 해당 게시글 (PostJpa와 관계 설정)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpa user; // 작성자 (UserJpa와 관계 설정)
}
