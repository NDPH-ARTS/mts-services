package uk.ac.ox.ndph.mts.site_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an invariant is violated retrieving from the store (not from the client end - that would
 * be a {@link ValidationException}.
 */
@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
public class InvariantException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6563074749881235497L;

    /**
     *
     * @param message The exception message
     */
    public InvariantException(String message) {
        super(message);
    }
}
