package LacunaMatata.Lacuna.exception.auth;

public class NotMatchPasswordException extends RuntimeException{
    public NotMatchPasswordException(String message) {
        super(message);
    }
}
