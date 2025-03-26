package community.Util;

import community.Exception.PostException.PostException;
import community.Exception.UserException.UserException;
import community.Model.Comment;
import community.Model.Post;
import community.Model.User;
import community.Repository.CommentRepository;
import community.Repository.PostRepository;
import community.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityValidator {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public User getValidUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .filter(user -> user.getDeletedAt() == null)
                .orElseThrow(UserException.UserNotFoundException::new);
    }

    public Post getValidPostOrThrow(String postId) {
        return postRepository.findById(postId)
                .filter(post -> post.getDeletedAt() == null)
                .orElseThrow(PostException.PostNotFoundException::new);
    }

    public Comment getValidCommentOrThrow(String commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(PostException.PostNotFoundException::new);
    }

    public void validatePostOwner(Post post, User user) {
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new PostException.PostNotMatchUserException();
        }
    }

    public void validateCommentOwner(Comment comment, User user) {
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new PostException.CommentNotMatchUserException();
        }
    }
}
