package uk.ac.ox.ndph.mts.roleserviceclient;

import okhttp3.mockwebserver.MockResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.WebClientConfig;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RoleIdExistsTest {

    public static MockWebServerWrapper webServer;
    private RoleServiceClient sut;
    private static WebClient.Builder builder;

    @BeforeAll
    static void beforeAll() throws IOException {
        final WebClientConfig config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        builder = config.webClientBuilder();

        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        sut = new RoleServiceClient(builder, webServer.getUrl());
    }

    @AfterAll
    static void afterAll() throws IOException {
        webServer.shutdown();
    }

    @Test
    void whenHttpSuccess_thenTrue() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.SC_OK));

        // Act
        boolean idExists = sut.roleIdExists("12");

        // Assert
        assertTrue(idExists);
    }

    @Test
    void whenHttpStatus404NotFound_thenFalse() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.SC_NOT_FOUND));

        // Act
        boolean idExists = sut.roleIdExists("12");

        // Assert
        assertFalse(idExists);
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR));

        // Act + Assert
        Assertions.assertThrows(RestException.class, () -> sut.roleIdExists("12"));
    }

}
