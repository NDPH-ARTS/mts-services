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
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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

    private List<RoleDTO> createManyRoles(final List<? extends RoleDTO> entities,
                                          final Consumer<HttpHeaders> authHeaders) throws Exception {
        Objects.requireNonNull(entities, ResponseMessages.LIST_NOT_NULL);
        Exception error = null;
        final List<RoleDTO> result = new ArrayList<>();
        for (final RoleDTO role : entities) {
            try {
                result.add(roleServiceClient.createEntity(role, authHeaders));
            } catch (Exception ex) {
                error = ex;
            }
        }
        if (error != null) {
            throw error;
        }
        return result;
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
                createManyRoles(Arrays.asList(role, role2), RoleServiceClient.noAuth());
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
            () -> createManyRoles(Collections.singletonList(role), RoleServiceClient.noAuth()));
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
            () -> createManyRoles(Arrays.asList(role, role2), RoleServiceClient.noAuth()));
    }

    @Test
     void whenDependentServiceFailsWhenNull_CorrectException() {
         assertThrows(Exception.class, () -> createManyRoles(null, RoleServiceClient.noAuth()));
     }

     @Test
     void whenDependentServiceFails_CorrectException() {
         RoleDTO testRole = new RoleDTO();
         testRole.setId("testId");

         webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

         List<RoleDTO> roles = Collections.singletonList(testRole);
         assertThrows(Exception.class, () -> createManyRoles(roles, RoleServiceClient.bearerAuth(token)));
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
             createManyRoles(roles, RoleServiceClient.bearerAuth(token));
         } catch(Exception e) {
             fail("Should not have thrown any exception");
         }
     }
}
