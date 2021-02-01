package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.TestRolesServiceBackend;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.WebClientConfig;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

class WebFluxRoleServiceClientTest {

    private TestRolesServiceBackend mockBackEnd;

    private RoleServiceClient client;

    private static WebClient.Builder builder;

    @BeforeAll
    static void init() {
        final WebClientConfig config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        builder = config.webClientBuilder();
    }

    @BeforeEach
    void setUp()  {
        this.mockBackEnd = TestRolesServiceBackend.autoStart();
        this.client = new WebFluxRoleServiceClient(builder, mockBackEnd.getUrl());
    }

    @AfterEach
    void cleanup() {
        this.mockBackEnd.shutdown();
    }

    @Test
    void roleIdExists_whenRoleExists_returnsTrue() {
        // Arrange
        final var roleId = "testRoleId";
        this.mockBackEnd.queueRoleResponse(roleId);
        // Act
        // Assert
        assertThat(client.roleIdExists(roleId), is(true));
    }

    @Test
    void roleIdExists_whenRoleDoesNotExist_returnsFalse() {
        // Arrange
        final var roleId = "testRoleId";
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_NOT_FOUND);
        // Act
        // Assert
        assertThat(client.roleIdExists(roleId), is(false));
    }

    @Test
    void roleIdExists_whenServiceFails_throwsException() {
        // Arrange
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.roleIdExists("someId"));
    }

    @Test
    void roleIdExists_whenConnectTimesOut_throwsException() {
        // Arrange
        final var roleId = "someId";
        this.mockBackEnd.queueRoleResponse(roleId, 2);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.roleIdExists(roleId));
    }

}
