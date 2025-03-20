package community.Model.JpaModel;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "post") // 테이블명 지정
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostJpa extends BaseTimeJpa {

    @Id
    @Column(name = "post_id", nullable = false, unique = true)
    private String postId; // 게시글 ID

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 내용

    @Column(name = "content_image")
    private String contentImage; // 이미지 URL

    @Column(name = "like_count", nullable = false)
    private int likeCount; // 좋아요 수

    @Column(name = "visit_count", nullable = false)
    private int visitCount; // 조회수

    @Column(name = "comment_count", nullable = false)
    private int commentCount; // 댓글 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpa user; // 작성자 ID (UserJpa와 관계 설정)

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt; // 소프트 삭제 필드
}
