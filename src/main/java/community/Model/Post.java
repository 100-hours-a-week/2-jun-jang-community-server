package community.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@Entity
@Table(name = "post") // 테이블명 지정
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Post extends BaseTime {

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
    private User user; // 작성자 ID (UserJpa와 관계 설정)

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt; // 소프트 삭제 필드

    public void increaseVisitCount() {
        this.visitCount += 1;
    }

    public void increaseCommentCount() {
        this.commentCount += 1;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1;
        }
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }
    
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }
}
