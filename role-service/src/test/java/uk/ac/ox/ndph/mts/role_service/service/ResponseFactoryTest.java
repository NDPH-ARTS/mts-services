package uk.ac.ox.ndph.mts.role_service.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


import ch.qos.logback.classic.Logger;

public class ResponseFactoryTest {

    private ListAppender<ILoggingEvent> appender;
    private Logger appLogger = (Logger) LoggerFactory.getLogger(ResponseFactory.class);


    @Test
    public void testWhenCreateResponseException_LogsMessage() {
        String logMessage = "some log message e.g. about permissions";
        ResponseStatusException ex = ResponseFactory.loggedException(
                HttpStatus.BAD_REQUEST,
                logMessage);

        assertTrue(appender.list.stream().anyMatch(msg -> msg.getMessage().contains(logMessage)));
    }

    @BeforeEach
    public void setUp() {
        appender = new ListAppender<>();
        appender.start();
        appLogger.addAppender(appender);
    }

    @AfterEach
    public void tearDown() {
        appLogger.detachAppender(appender);
    }



    /*
    @Test
    public void testLogging(){

        String logMessage = "some log message e.g. about permissions";
        TestLogger logger = TestLoggerFactory.getTestLogger(ResponseFactory.class);

        ResponseStatusException ex = ResponseFactory.loggedException(
                HttpStatus.BAD_REQUEST,
                logMessage);

        assertTrue(logger.getLoggingEvents().stream().anyMatch(event-> event.getMessage().contains(logMessage)));

    }*/

}
