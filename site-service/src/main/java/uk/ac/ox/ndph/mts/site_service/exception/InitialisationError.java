package uk.ac.ox.ndph.mts.site_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an error during initialisation is raised.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InitialisationError extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 4610719258813716084L;

    /**
     *
     * @param message the exception message
     * @param internal the internal exception
     */
    public InitialisationError(String message, Exception internal) {
        super(message, internal);
    }

    /**
     *
     * @param message the exception message
     */
    public InitialisationError(String message) {
        super(message);
    }
}
