package uk.ac.ox.ndph.mts.practitioner_service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.TestRolesServiceBackend;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PageableResult;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import java.net.HttpURLConnection;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties =  { "http.readTimeOutMs=1000", "http.connectTimeOutMs=500" })
@ContextConfiguration
public class WebFluxRoleServiceClientTest {

    private TestRolesServiceBackend mockBackEnd;

    private RoleServiceClient client;

    @Autowired
    private WebClient.Builder builder;

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
    void getRoles_whenRolesExist_returnsRoleList() {
        // Arrange
        final var roleId = "testRoleId";
        this.mockBackEnd.queueRolesResponse(roleId);
        // Act
        final Collection<RoleDTO> result = client.getRoles();
        // Assert
        assertThat(result, is(iterableWithSize(1)));
        assertThat(result, everyItem(hasProperty("id", equalTo(roleId))));
    }

    @Test
    void getRoles_whenNoValidRolesExist_returnsEmptyList() throws JsonProcessingException {
        // Arrange
        this.mockBackEnd.queueResponse(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(PageableResult.singleton(new RoleDTO())))
                .addHeader("Content-Type", "application/json"));
        // Act
        final Collection<RoleDTO> result = client.getRoles();
        // Assert
        assertThat(result, is(iterableWithSize(0)));
    }

    @Test
    void getRoles_whenNoRolesExist_returnsEmptyList() {
        // Arrange
        this.mockBackEnd.queueRolesResponse(null);
        // Act
        final Collection<RoleDTO> result = client.getRoles();
        // Assert
        assertThat(result, is(iterableWithSize(0)));
    }

    @Test
    void getRole_whenRoleExists_returnsRole() {
        // Arrange
        final var roleId = "testRoleId";
        this.mockBackEnd.queueRoleResponse(roleId);
        // Act
        final RoleDTO result = client.getRole(roleId);
        // Assert
        assertThat(result, (hasProperty("id", equalTo(roleId))));
    }

    @Test
    void getRole_whenRoleDoesNotExit_throwsRestException() {
        // Arrange
        final var roleId = "testRoleId";
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_NOT_FOUND);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRole(roleId));
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
    void getRoles_whenServiceFails_throwsException() {
        // Arrange
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRoles());
    }

    @Test
    void getRole_whenServiceFails_throwsException() {
        // Arrange
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRole("someId"));
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
    void getRole_whenConnectTimesOut_throwsException() {
        // Arrange
        final var roleId = "someId";
        this.mockBackEnd.queueRoleResponse(roleId, 2);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRole(roleId));
    }

}
