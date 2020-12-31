package uk.ac.ox.ndph.mts.practitioner_service.exception;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class RestException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 518857235382506092L;

    public RestException(String message) {
        super(message);
    }
}