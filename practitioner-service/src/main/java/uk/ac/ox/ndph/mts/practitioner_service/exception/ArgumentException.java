package uk.ac.ox.ndph.mts.practitioner_service.exception;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ArgumentException extends RuntimeException{
    public ArgumentException(String message) {
        super(message);
    }
}