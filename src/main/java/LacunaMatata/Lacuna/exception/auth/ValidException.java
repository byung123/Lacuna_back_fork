package LacunaMatata.Lacuna.exception.auth;


import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

public class ValidException extends RuntimeException{

    @Getter
    private List<FieldError> fieldErrorList;

    public ValidException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrorList = fieldErrors;
    }
}
