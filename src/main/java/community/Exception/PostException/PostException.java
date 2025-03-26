package community.Exception.PostException;

import community.Common.ErrorMessageCode;

public class PostException {

    public static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException() {
            super(ErrorMessageCode.POST_NOT_FOUND.getMessage());
        }
    }

    public static class PostNotMatchUserException extends RuntimeException {
        public PostNotMatchUserException() {
            super(ErrorMessageCode.POST_NOT_OWNER.getMessage());
        }
    }

    public static class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException() {
            super(ErrorMessageCode.COMMENT_NOT_FOUND.getMessage());
        }
    }

    public static class CommentNotMatchUserException extends RuntimeException {
        public CommentNotMatchUserException() {
            super(ErrorMessageCode.COMMENT_NOT_OWNER.getMessage());
        }
    }
}
