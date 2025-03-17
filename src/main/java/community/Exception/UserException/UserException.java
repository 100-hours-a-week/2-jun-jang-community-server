package community.Exception.UserException;

import lombok.NoArgsConstructor;

public class UserException {

    @NoArgsConstructor  // 기본 생성자 자동 생성
    public static class LoginFailedException extends RuntimeException {
        public LoginFailedException(String message) {
            super(message);  // 부모 클래스(RuntimeException)의 생성자로 메시지를 전달
        }
    }
    @NoArgsConstructor
    public static class UserIsValidException extends RuntimeException {
        public UserIsValidException(String message) {
            super(message);
        }
    }
    @NoArgsConstructor
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}

