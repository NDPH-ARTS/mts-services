package uk.ac.ox.ndph.mts.roleserviceclient;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IdExistsTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private RoleServiceClient roleServiceClient;

    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        roleServiceClient = builder.build(webServer.getUrl());
    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void whenHttpSuccess_thenTrue() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.OK.value()));

        // Act
        boolean idExists = roleServiceClient.idExists("12", RoleServiceClient.noAuth());

        // Assert
        assertTrue(idExists);
    }

    @Test
    void whenHttpStatus404NotFound_thenFalse() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

        // Act
        boolean idExists = roleServiceClient.idExists("12", RoleServiceClient.noAuth());

        // Assert
        assertFalse(idExists);
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        // Act + Assert
        Assertions.assertThrows(RestException.class, () -> roleServiceClient.idExists("12", RoleServiceClient.noAuth()));
    }

}
