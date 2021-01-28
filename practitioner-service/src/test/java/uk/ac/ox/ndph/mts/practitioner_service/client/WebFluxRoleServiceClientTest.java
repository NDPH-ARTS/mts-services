package uk.ac.ox.ndph.mts.practitioner_service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PageableResult;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import java.net.HttpURLConnection;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

public class WebFluxRoleServiceClientTest {

    private MockWebServer mockBackEnd;
    private RoleServiceClient client;

    @BeforeEach
    void setUp() throws Exception {
        this.mockBackEnd = new MockWebServer();
        this.mockBackEnd.start();
        this.client = new WebFluxRoleServiceClient(WebClient.builder(), String.format("http://localhost:%s", mockBackEnd.getPort()));
    }

    public static void queueRolesResponse(final MockWebServer backend, final String roleId) {
        try {
            final var roleObj = new RoleDTO();
            if(roleId != null) {
                roleObj.setId(roleId);
            }
            final var response = (roleId == null) ? PageableResult.empty() :  PageableResult.singleton(roleObj);
            backend.enqueue(new MockResponse()
                    .setBody(new ObjectMapper().writeValueAsString(response))
                    .addHeader("Content-Type", "application/json"));
        } catch(RuntimeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void queueRolesResponse(final String roleId) {
        queueRolesResponse(this.mockBackEnd, roleId);
    }

    public static void queueRoleResponse(final MockWebServer backend, final String roleId) {
        try {
            final var roleObj = new RoleDTO();
            if (roleId != null) {
                roleObj.setId(roleId);
            }
            backend.enqueue(new MockResponse()
                    .setBody(new ObjectMapper().writeValueAsString(roleObj))
                    .addHeader("Content-Type", "application/json"));
        } catch(RuntimeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void queueRoleResponse(final String roleId) {
        queueRoleResponse(this.mockBackEnd, roleId);
    }

    public static void queueErrorResponse(final MockWebServer backend, final int errorCode) {
        try {
            backend.enqueue(new MockResponse()
                    .setResponseCode(errorCode)
                    .setBody(errorCode + " error"));
        } catch(RuntimeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private void queueErrorResponse(final int errorCode) {
        queueErrorResponse(this.mockBackEnd, errorCode);
    }

    @Test
    void getRoles_whenRolesExist_returnsRoleList() {
        // Arrange
        final var roleId = "testRoleId";
        queueRolesResponse(roleId);
        // Act
        final Collection<RoleDTO> result = client.getRoles();
        // Assert
        assertThat(result, is(iterableWithSize(1)));
        assertThat(result, everyItem(hasProperty("id", equalTo(roleId))));

    }

    @Test
    void getRoles_whenNoRolesExist_returnsEmptyList() {
        // Arrange
        final var roleId = "testRoleId";
        queueRolesResponse(null);
        // Act
        final Collection<RoleDTO> result = client.getRoles();
        // Assert
        assertThat(result, is(iterableWithSize(0)));
    }

    @Test
    void getRole_whenRoleExists_returnsRole() {
        // Arrange
        final var roleId = "testRoleId";
        queueRoleResponse(roleId);
        // Act
        final RoleDTO result = client.getRole(roleId);
        // Assert
        assertThat(result, (hasProperty("id", equalTo(roleId))));
    }

    @Test
    void getRole_whenRoleDoesNotExit_throwsRestException() {
        // Arrange
        final var roleId = "testRoleId";
        queueErrorResponse(HttpURLConnection.HTTP_NOT_FOUND);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRole(roleId));
    }

    @Test
    void roleIdExists_whenRoleExists_returnsTrue() {
        // Arrange
        final var roleId = "testRoleId";
        queueRoleResponse(roleId);
        // Act
        // Assert
        assertThat(client.roleIdExists(roleId), is(true));
    }

    @Test
    void roleIdExists_whenRoleDoesNotExist_returnsFalse() {
        // Arrange
        final var roleId = "testRoleId";
        queueErrorResponse(HttpURLConnection.HTTP_NOT_FOUND);
        // Act
        // Assert
        assertThat(client.roleIdExists(roleId), is(false));
    }

    @Test
    void getRoles_whenServiceFails_throwsException() {
        // Arrange
        queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRoles());
    }

    @Test
    void getRole_whenServiceFails_throwsException() {
        // Arrange
        queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRole("someId"));
    }


    @Test
    void roleIdExists_whenServiceFails_throwsException() {
        // Arrange
        queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.roleIdExists("someId"));
    }

}
