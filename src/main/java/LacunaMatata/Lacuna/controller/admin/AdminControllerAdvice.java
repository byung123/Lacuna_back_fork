package LacunaMatata.Lacuna.controller.admin;

import LacunaMatata.Lacuna.exception.admin.NotFoundDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdminControllerAdvice {

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<?> NotFoundDataException(NotFoundDataException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
