package uk.ac.ox.ndph.mts.site_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an incoming GET-by-id call fails
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 618857235382506092L;

    private final String id;

    /**
     * Create a new instance with the given message and ID
     * @param message the exception message
     * @param id the ID that was searched for and not found (could be null if not known at throw site)
     */
    public NotFoundException(final String message, final String id) {
        super(message);
        this.id = id;
    }

    /**
     * Create a new instance with the given message ID, and causitive exception
     * @param message the exception message
     * @param id the ID that was searched for and not found (could be null if not known at throw site)
     * @param cause the causitive exception
     */
    public NotFoundException(final String message, final String id, final Exception cause) {
        super(message, cause);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}
