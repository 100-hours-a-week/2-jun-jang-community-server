package community.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment") // 테이블명 지정
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment extends BaseTime {

    @Id
    @Column(name = "comment_id", nullable = false, unique = true)
    private String commentId; // 댓글 ID

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 해당 게시글 (PostJpa와 관계 설정)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자 (UserJpa와 관계 설정)
}
