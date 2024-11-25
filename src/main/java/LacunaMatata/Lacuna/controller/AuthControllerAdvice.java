package LacunaMatata.Lacuna.controller;

import LacunaMatata.Lacuna.exception.InactiveAccountException;
import LacunaMatata.Lacuna.exception.auth.EmailNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<?> InactiveAccountException(InactiveAccountException e) {
        return ResponseEntity.status(401).body(e.getErrorMessages());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> EmailNotFoundException(EmailNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

}
