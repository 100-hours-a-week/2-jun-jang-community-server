package community.Exception.PostException;

import lombok.NoArgsConstructor;

public class PostException {
    @NoArgsConstructor
    public static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(String message) {
            super(message);
        }
    }
    @NoArgsConstructor
    public static class PostNotMatchUserException extends RuntimeException {
        public PostNotMatchUserException(String message) {
            super(message);
        }
    }
    @NoArgsConstructor
    public static class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException(String message) {
            super(message);
        }
    }
    @NoArgsConstructor
    public static class CommentNotMatchUserException extends RuntimeException {
        public CommentNotMatchUserException(String message) {
            super(message);
        }
    }
}
