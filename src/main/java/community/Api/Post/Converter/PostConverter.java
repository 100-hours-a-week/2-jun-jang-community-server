package community.Api.Post.Converter;

import community.Api.Post.Dtos.PostResponse;
import community.Model.Comment;
import community.Model.Post;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


    public static PostResponse.CreatePostResponse toCreatePostResponse(Post post) {
        return PostResponse.CreatePostResponse.builder()
                .postId(post.getPostId())
                .build();
    }


    public static PostResponse.GetPostResponse toGetPostResponse(Post post, String userName, String userProfileImage, Boolean isLike) {
        return PostResponse.GetPostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUser().getUserId())
                .userName(userName)
                .userProfileImage(userProfileImage)
                .contentImage(post.getContentImage())
                .createdAt(post.getCreatedAt().format(formatter))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .visitCount(post.getVisitCount())
                .isLike(isLike)
                .build();
    }


    public static PostResponse.GetPostsResponse toGetPostsResponse(List<PostResponse.PostsItem> posts) {

        return PostResponse.GetPostsResponse.builder()
                .posts(posts)
                .build();
    }


    public static PostResponse.PostsItem toPostsItem(Post post, String userName, String userProfileImage) {
        return PostResponse.PostsItem.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .visitCount(post.getVisitCount())
                .userId(post.getUser().getUserId())
                .userName(userName)
                .userProfileImage(userProfileImage)
                .createdAt(post.getCreatedAt().format(formatter))
                .build();
    }


    public static PostResponse.CreateCommentResponse toCreateCommentResponse(Comment comment) {
        return PostResponse.CreateCommentResponse.builder()
                .postId(comment.getPost().getPostId())
                .commentId(comment.getCommentId())
                .build();
    }

    public static PostResponse.GetCommentsResponse toGetCommentsResponse(List<PostResponse.CommentItem> list) {
        return PostResponse.GetCommentsResponse.builder()
                .comments(list)
                .build();
    }

    public static PostResponse.CommentItem toCommentItem(Comment comment) {
        return PostResponse.CommentItem.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .content(comment.getContent())
                .userName(comment.getUser().getNickname())
                .userProfileImage(comment.getUser().getUserProfile())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }
}
