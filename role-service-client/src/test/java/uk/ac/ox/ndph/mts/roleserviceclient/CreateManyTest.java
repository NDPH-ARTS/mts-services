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
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreateManyTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private RoleServiceClient roleServiceClient;
    private static String token = "123ert";

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
    void whenCreateSucceeds_responseIdsMatch() throws Exception {
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
        assertThrows(Exception.class,
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
        assertThrows(Exception.class,
            () -> roleServiceClient.createMany(Arrays.asList(role, role2), RoleServiceClient.noAuth()));
    }

    @Test
     void whenDependentServiceFailsWhenNull_CorrectException() {
         assertThrows(Exception.class, () -> roleServiceClient.createMany(null, RoleServiceClient.noAuth()));
     }

     @Test
     void whenDependentServiceFails_CorrectException() {
         RoleDTO testRole = new RoleDTO();
         testRole.setId("testId");

         webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

         List<RoleDTO> roles = Collections.singletonList(testRole);
         assertThrows(Exception.class, () -> roleServiceClient.createMany(roles, RoleServiceClient.bearerAuth(token)));
     }

     @Test
     void testRoleService_WithList_WhenValidInput() throws IOException {
         RoleDTO testRole = new RoleDTO();
         testRole.setId("testId");

         PermissionDTO testPermission = new PermissionDTO();
         testPermission.setId("testId");

         testRole.setPermissions(Collections.singletonList(testPermission));

         List<RoleDTO> roles = Collections.singletonList(testRole);
         final ObjectMapper mapper = new ObjectMapper();
         webServer.queueResponse(mapper.writeValueAsString(testRole));
         try {
             roleServiceClient.createMany(roles, RoleServiceClient.bearerAuth(token));
         } catch(Exception e) {
             fail("Should not have thrown any exception");
         }
     }
}
