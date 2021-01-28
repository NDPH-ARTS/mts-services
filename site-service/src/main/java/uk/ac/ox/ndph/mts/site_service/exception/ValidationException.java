package uk.ac.ox.ndph.mts.site_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when validation failed for an attribute value
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6563074749881235496L;
    
    /**
     *
     * @param message The exception message
     */
    public ValidationException(String message) {
        super(message);
    }
}
