package uk.ac.ox.ndph.mts.role_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LoggedRoleServiceException extends ResponseStatusException {

    private final Logger logger = LoggerFactory.getLogger(LoggedRoleServiceException.class);

    public LoggedRoleServiceException(HttpStatus status, String reason) {
        super(status, reason);
        logger.warn(String.format("Role-service exception: %s.  Responding with code %s", reason, status.value()));
    }
}
