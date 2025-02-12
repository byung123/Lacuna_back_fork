package LacunaMatata.Lacuna.controller.user;

import LacunaMatata.Lacuna.exception.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {
    @ExceptionHandler(NotSelectItemException.class)
    public ResponseEntity<?> NotSelectItemException(NotSelectItemException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotMatchPriceException.class)
    public ResponseEntity<?> NotMatchPriceException(NotMatchPriceException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

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
