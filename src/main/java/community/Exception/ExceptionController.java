package community.Exception;

import community.Exception.JwtException.JwtException;
import community.Exception.PostException.PostException;
import community.Exception.UserException.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(UserException.LoginFailedException.class)
    public ResponseEntity<ExceptionResponse> handleLoginException(UserException.LoginFailedException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("401");
        response.setMessage("로그인 실패: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserException.UserIsValidException.class)
    public ResponseEntity<ExceptionResponse> handleUserIsValidException(UserException.UserIsValidException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("400");
        response.setMessage("회원가입 실패: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserException.UserNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("404");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //게시물 관련
    @ExceptionHandler(PostException.PostNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePostNotFoundException(PostException.PostNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("404");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostException.PostNotMatchUserException.class)
    public ResponseEntity<ExceptionResponse> handlePostNotMatchUserException(PostException.PostNotMatchUserException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("400");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostException.CommentNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCommentNotFoundException(PostException.CommentNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("404");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostException.CommentNotMatchUserException.class)
    public ResponseEntity<ExceptionResponse> handleCommentNotMatchUserException(PostException.CommentNotMatchUserException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("400");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //중복 요청
    @ExceptionHandler(JwtException.DuplicateRequestException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateRequestException(JwtException.DuplicateRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setCode("429");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);

    }
}
