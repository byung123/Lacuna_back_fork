package LacunaMatata.Lacuna.exception.auth;

public class FailSendEmailException extends RuntimeException{
    public FailSendEmailException(String message) {
        super(message);
    }
}
