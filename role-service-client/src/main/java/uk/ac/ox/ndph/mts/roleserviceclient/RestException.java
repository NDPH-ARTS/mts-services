package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an outgoing REST call to a service failed
 */
@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class RestException extends RuntimeException {

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}
