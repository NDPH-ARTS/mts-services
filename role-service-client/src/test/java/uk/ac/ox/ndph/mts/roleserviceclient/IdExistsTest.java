package uk.ac.ox.ndph.mts.roleserviceclient;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class IdExistsTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    final Consumer<HttpHeaders> authHeaders = RoleServiceClient.noAuth();
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
        roleServiceClient = Mockito.spy(builder.build(webServer.getUrl()));
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
        boolean idExists = roleServiceClient.entityIdExists("12", authHeaders);

        // Assert
        assertTrue(idExists);
    }

    @Test
    void whenHttpStatus404NotFound_thenFalse() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

        // Act
        boolean idExists = roleServiceClient.entityIdExists("12", authHeaders);

        // Assert
        assertFalse(idExists);
    }


    @Test
    void whenServiceError_thenThrowRunTimeException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));


        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> roleServiceClient.entityIdExists("12", authHeaders));
    }

    @Test
    void TestEntityRoleExists_WhenServiceException_ReturnsOtherException() {
        doThrow(new RuntimeException()).when(roleServiceClient).entityIdExists("12", authHeaders);
        Assertions.assertThrows(RuntimeException.class, () -> roleServiceClient.entityIdExists("12", authHeaders));
    }


}
