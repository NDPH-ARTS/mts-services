package uk.ac.ox.ndph.mts.role_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseFactory {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFactory.class);
    private static final String logMessage = "Role-service exception: %s.  Responding with code %s";


    public static ResponseStatusException loggedException(HttpStatus status, String reason) {

        logger.warn(String.format(logMessage, reason, status.value()));
        return new ResponseStatusException(status, reason);
    }
}
