package community.Exception.JwtException;

import lombok.NoArgsConstructor;

public class JwtException {

    @NoArgsConstructor
    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }

    @NoArgsConstructor
    public static class TokenNotFoundException extends RuntimeException {
        public TokenNotFoundException(String message) {
            super(message);
        }
    }

    @NoArgsConstructor
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}
