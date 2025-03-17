package community.Model.JdbcModel;

import lombok.*;

@Data

@NoArgsConstructor
@Getter
@Setter
public class CommentJdbc extends BaseTimeJdbc {
    private String commentId;
    private String content;
    private String postId;
    private String userId;
    public CommentJdbc(String commentId, String content, String postId, String userId) {
        super();
        this.commentId = commentId;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }
}
