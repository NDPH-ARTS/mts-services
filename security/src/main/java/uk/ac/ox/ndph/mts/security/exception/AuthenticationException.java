package uk.ac.ox.ndph.mts.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an incoming REST call was forbidden
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AuthenticationException extends RuntimeException {

    /**
     *
     * @param message the exception message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     *
     * @param message the exception message
     * @param cause case of the exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
