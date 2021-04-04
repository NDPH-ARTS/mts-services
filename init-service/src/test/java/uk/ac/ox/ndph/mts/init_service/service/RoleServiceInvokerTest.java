package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
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

@ExtendWith(MockitoExtension.class)
public class RoleServiceInvokerTest {
    private static MockWebServer mockBackEnd;
    private static String token = "123ert";

    @Mock
    RoleServiceInvoker roleServiceInvoker;

    @Mock
    RoleServiceClient roleServiceClient;

    @BeforeAll
    static void setUp() throws IOException {
        // this section uses a custom webclient props
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }


    @BeforeEach
    void setUpEach() throws IOException {
        roleServiceInvoker = new RoleServiceInvoker(roleServiceClient);
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
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
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(role)));
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(role2)));
        final List<RoleDTO> actual =
                roleServiceInvoker.createManyRoles(Arrays.asList(role, role2), RoleServiceClient.noAuth());
        //Assert
        assertThat(actual.size(), equalTo(2));
//        assertThat(actual.get(0).getId(), equalTo(role.getId()));
   //     assertThat(actual.get(1).getId(), equalTo(role2.getId()));
    }

//    @Test
//    void whenServiceError_thenThrowRestException() {
//        // Arrange
//        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
//        final RoleDTO role = new RoleDTO();
//        role.setId("the-id");
//        role.setPermissions(Collections.emptyList());
//        // Act + Assert
//        assertThrows(Exception.class,
//                () -> roleServiceInvoker.createManyRoles(Collections.singletonList(role), RoleServiceClient.noAuth()));
//    }

//    @Test
//    void whenOneServiceError_thenThrowRestException() throws JsonProcessingException {
//        // Arrange
//        final RoleDTO role = new RoleDTO();
//        role.setId("the-id");
//        role.setPermissions(Collections.emptyList());
//        final RoleDTO role2 = new RoleDTO();
//        role2.setId("the-id-2");
//        role2.setPermissions(Collections.emptyList());
//        final ObjectMapper mapper = new ObjectMapper();
//        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(role)));
//        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
//        // Act + Assert
//        assertThrows(Exception.class,
//                () -> roleServiceInvoker.createManyRoles(Arrays.asList(role, role2), RoleServiceClient.noAuth()));
//    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> roleServiceInvoker.createManyRoles(null, RoleServiceClient.noAuth()));
    }

//    @Test
//    void whenDependentServiceFails_CorrectException() {
//        RoleDTO testRole = new RoleDTO();
//        testRole.setId("testId");
//
//        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
//
//        List<RoleDTO> roles = Collections.singletonList(testRole);
//        assertThrows(Exception.class, () -> roleServiceInvoker.createManyRoles(roles, RoleServiceClient.bearerAuth(token)));
//    }

    @Test
    void testRoleService_WithList_WhenValidInput() throws IOException {
        RoleDTO testRole = new RoleDTO();
        testRole.setId("testId");

        PermissionDTO testPermission = new PermissionDTO();
        testPermission.setId("testId");

        testRole.setPermissions(Collections.singletonList(testPermission));

        List<RoleDTO> roles = Collections.singletonList(testRole);
        final ObjectMapper mapper = new ObjectMapper();
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(testRole)));
        try {
            roleServiceInvoker.createManyRoles(roles, RoleServiceClient.bearerAuth(token));
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
