package LacunaMatata.Lacuna.exception.auth;

public class EmailNotFoundException extends RuntimeException{

    public EmailNotFoundException(String message) {
        super(message);
    }
}
