package LacunaMatata.Lacuna.security.handler.user;

import LacunaMatata.Lacuna.exception.user.userException.NotFoundMyMbtiResultException;
import LacunaMatata.Lacuna.exception.user.userException.NotFoundMyOrderInfoException;
import LacunaMatata.Lacuna.exception.user.userException.NotFoundUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<?> NotFoundUserException(NotFoundUserException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundMyMbtiResultException.class)
    public ResponseEntity<?> NotFoundMyMbtiResultException(NotFoundMyMbtiResultException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundMyOrderInfoException.class)
    public ResponseEntity<?> NotFoundMyOrderInfoException(NotFoundMyOrderInfoException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
