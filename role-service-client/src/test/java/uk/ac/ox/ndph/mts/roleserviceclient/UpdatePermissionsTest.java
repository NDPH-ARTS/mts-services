package uk.ac.ox.ndph.mts.roleserviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UpdatePermissionsTest {

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
    void whenUpdateSucceeds_responseMatchesId() throws JsonProcessingException {
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        final RoleDTO result = new RoleDTO();
        result.setId(role.getId());
        final PermissionDTO perm = new PermissionDTO();
        perm.setId("perm-id");
        result.setPermissions(Collections.singletonList(perm));
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(result));
        final RoleDTO actual =
            roleServiceClient.updatePermissions(role.getId(), result.getPermissions(), RoleServiceClient.noAuth());
        //Assert
        assertThat(actual.getId(), equalTo(result.getId()));
        assertThat(actual.getPermissions().size(), equalTo(result.getPermissions().size()));
        assertThat(actual.getPermissions().get(0).getId(),
            equalTo(result.getPermissions().get(0).getId()));
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        // Act + Assert
        assertThrows(RestException.class, () ->
            roleServiceClient.updatePermissions("id", Collections.emptyList(), RoleServiceClient.noAuth()));
    }

    @Test
    void whenParamError_thenThrowsNPE() {
        assertThrows(NullPointerException.class, () ->
            roleServiceClient.updatePermissions(null, Collections.emptyList(), RoleServiceClient.noAuth()));
        assertThrows(NullPointerException.class, () ->
            roleServiceClient.updatePermissions("id", null, RoleServiceClient.noAuth()));
    }

}
