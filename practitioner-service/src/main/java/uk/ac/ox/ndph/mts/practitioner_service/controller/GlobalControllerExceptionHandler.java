package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    /* Allows RestExceptions to be thrown without doing a log then re-throw */
    @ExceptionHandler(RestException.class)
    public void handleException(final RestException ex) {
        logger.error("Handling RestException", ex);
        throw ex;
    }
}
