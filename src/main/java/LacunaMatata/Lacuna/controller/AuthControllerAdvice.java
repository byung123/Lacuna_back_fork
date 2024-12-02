package LacunaMatata.Lacuna.controller;

import LacunaMatata.Lacuna.exception.InactiveAccountException;
import LacunaMatata.Lacuna.exception.auth.*;
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> UsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(NotMatchAuthenticationException.class)
    public ResponseEntity<?> NotMatchAuthenticationException(NotMatchAuthenticationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotMatchPasswordCheckException.class)
    public ResponseEntity<?> NotMatchPasswordCheckException(NotMatchPasswordCheckException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IsPresentPasswordException.class)
    public ResponseEntity<?> IsPresentPasswordException(IsPresentPasswordException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> Exception(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
