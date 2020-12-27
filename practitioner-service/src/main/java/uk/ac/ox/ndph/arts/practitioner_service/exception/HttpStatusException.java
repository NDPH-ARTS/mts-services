package uk.ac.ox.ndph.arts.practitioner_service.exception;

import org.springframework.http.HttpStatus;

public abstract class HttpStatusException extends RuntimeException {

    public HttpStatusException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();

}