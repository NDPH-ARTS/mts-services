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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreateEntityTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private RoleServiceClient roleServiceClient;
    private String token = "some-token";
    private Consumer<HttpHeaders> authHeaders = RoleServiceClient.bearerAuth(token);

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
    void whenCreateSucceeds_responseMatchesId() throws JsonProcessingException {
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(role));
        final RoleDTO actual = roleServiceClient.createEntity(role, authHeaders);
        //Assert
        assertThat(actual.getId(), equalTo(role.getId()));
        assertThat(actual.getPermissions(), containsInAnyOrder(role.getPermissions().toArray()));
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        // Act + Assert
        assertThrows(Exception.class, () -> roleServiceClient.createEntity(role, authHeaders));
    }

    @Test
     void testRoleService_WithRoleNoPermissions_WhenValidInput() throws IOException {
        RoleDTO testRole = new RoleDTO();
        testRole.setId("testId");
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(testRole));
        var returnedRoleId = roleServiceClient.createEntity(testRole, authHeaders);
        assertNotNull(returnedRoleId);
     }

}
