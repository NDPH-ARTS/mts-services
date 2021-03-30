package uk.ac.ox.ndph.mts.role_service.service;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseFactoryTest {


    @Test
    public void testLogging(){

        String logMessage = "some log message e.g. about permissions";
        TestLogger logger = TestLoggerFactory.getTestLogger(ResponseFactory.class);

        ResponseStatusException ex = ResponseFactory.loggedException(
                HttpStatus.BAD_REQUEST,
                logMessage);

        assertTrue(logger.getLoggingEvents().stream().anyMatch(event-> event.getMessage().contains(logMessage)));

    }

}
