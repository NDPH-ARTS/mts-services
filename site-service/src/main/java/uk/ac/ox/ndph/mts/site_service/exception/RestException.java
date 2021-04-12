package uk.ac.ox.ndph.mts.site_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an outgoing REST call to a dependant service failed
 */
@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class RestException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 518857235382506092L;

    /**
     *
     * @param message the exception message
     */
    public RestException(String message) {
        super(message);
    }

    /**
     *
     * @param message the exception message
     * @param innerException the inter exception
     */
    public RestException(String message, Exception innerException) {
        super(message, innerException);
    }
}
