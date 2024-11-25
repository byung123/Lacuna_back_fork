package LacunaMatata.Lacuna.exception.auth;

public class NotMatchAuthenticationException extends RuntimeException{
    public NotMatchAuthenticationException(String message) {
        super(message);
    }
}
