package community.Model.JdbcModel;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
public class PostJdbc extends BaseTimeJdbc {
    private String postId;       // 게시글 ID
    private String title;        // 제목
    private String content;      // 내용
    private String contentImage;   // 이미지 URL
    private int likeCount;       // 좋아요 수
    private int visitCount;      // 조회수
    private int commentCount;    // 댓글 수
    private String userId;       // 작성자 ID
    private OffsetDateTime deletedAt; // 소프트 삭제를 위한 필드
    public PostJdbc(String postId, String title, String content, String contentImage, int likeCount, int visitCount, int commentCount, String userId, OffsetDateTime deletedAt) {
        super();
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.contentImage = contentImage;
        this.likeCount = likeCount;
        this.visitCount = visitCount;
        this.commentCount = commentCount;
        this.userId = userId;
        this.deletedAt = deletedAt;

    }
}
