package LacunaMatata.Lacuna.exception.auth;

public class TokenValidExpiredException extends RuntimeException{
    public TokenValidExpiredException(String message) {
        super(message);
    }
}
