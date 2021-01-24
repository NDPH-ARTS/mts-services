package uk.ac.ox.ndph.mts.trial_config_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class DependentServiceException extends RuntimeException {

    public DependentServiceException(String message) {
        super(message);
    }
}