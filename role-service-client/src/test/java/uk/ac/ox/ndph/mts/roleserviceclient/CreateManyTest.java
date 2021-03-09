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
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreateManyTest {

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
    void whenCreateSucceeds_responseIdsMatch() throws JsonProcessingException {
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        final RoleDTO role2 = new RoleDTO();
        role2.setId("the-id-2");
        role2.setPermissions(Collections.emptyList());
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(role));
        webServer.queueResponse(mapper.writeValueAsString(role2));
        final List<RoleDTO> actual =
            roleServiceClient.createMany(Arrays.asList(role, role2), RoleServiceClient.noAuth());
        //Assert
        assertThat(actual.size(), equalTo(2));
        assertThat(actual.get(0).getId(), equalTo(role.getId()));
        assertThat(actual.get(1).getId(), equalTo(role2.getId()));
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        // Act + Assert
        assertThrows(RestException.class,
            () -> roleServiceClient.createMany(Collections.singletonList(role), RoleServiceClient.noAuth()));
    }

    @Test
    void whenOneServiceError_thenThrowRestException() throws JsonProcessingException {
        // Arrange
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        final RoleDTO role2 = new RoleDTO();
        role2.setId("the-id-2");
        role2.setPermissions(Collections.emptyList());
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(role));
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        // Act + Assert
        assertThrows(RestException.class,
            () -> roleServiceClient.createMany(Arrays.asList(role, role2), RoleServiceClient.noAuth()));
    }

}