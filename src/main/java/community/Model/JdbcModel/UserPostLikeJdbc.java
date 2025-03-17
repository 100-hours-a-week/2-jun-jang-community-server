package community.Model.JdbcModel;

import lombok.*;

@Data

@NoArgsConstructor
@Getter
@Setter
public class UserPostLikeJdbc extends BaseTimeJdbc {
    private String postId;
    private String userId;
    private String likeId;
    private boolean isLike;
    public UserPostLikeJdbc(String likeId, boolean isLike, String postId, String userId) {
        super();
        this.postId = postId;
        this.userId = userId;
        this.isLike = isLike;
        this.likeId = likeId;
    }
}
