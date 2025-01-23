package LacunaMatata.Lacuna.controller.user;

import LacunaMatata.Lacuna.exception.user.NotMatchPriceException;
import LacunaMatata.Lacuna.exception.user.NotSelectItemException;
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
}
