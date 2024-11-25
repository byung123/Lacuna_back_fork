package LacunaMatata.Lacuna.exception.auth;

public class NotMatchPasswordCheckException extends RuntimeException{
    public NotMatchPasswordCheckException(String message) {
        super(message);
    }
}
