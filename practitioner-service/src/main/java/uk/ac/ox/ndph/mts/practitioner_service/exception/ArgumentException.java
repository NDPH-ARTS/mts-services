package uk.ac.ox.ndph.mts.practitioner_service.exception;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ArgumentException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 3516386043823538882L;

    public ArgumentException(String message) {
        super(message);
    }
}