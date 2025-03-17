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
}
