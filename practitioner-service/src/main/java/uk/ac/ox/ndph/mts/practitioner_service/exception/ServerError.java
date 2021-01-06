package uk.ac.ox.ndph.mts.practitioner_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an outgoing REST call to a dependant service failed
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerError extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 4610719258813716084L;

    /**
     *
     * @param message the exception message
     * @param internal the internal exception
     */
    public ServerError(String message, Exception internal) {
        super(message, internal);
    }

    /**
     *
     * @param message the exception message
     */
    public ServerError(String message) {
        super(message);
    }
}
