package uk.ac.ox.ndph.arts.practitioner_service.exception;

import org.springframework.http.HttpStatus;

public class RestException extends HttpStatusException{
    public RestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_GATEWAY;
    }
}