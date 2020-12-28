package uk.ac.ox.ndph.arts.practitioner_service.exception;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class RestException extends RuntimeException{
    public RestException(String message) {
        super(message);
    }
}