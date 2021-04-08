package uk.ac.ox.ndph.mts.init_service.service;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceInvokerTest {
    private static MockWebServer mockBackEnd;
    private static String token = "123ert";
    private static Consumer<HttpHeaders> authHeaders = RoleServiceClient.noAuth();

    @InjectMocks
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
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }


    @Test
    void whenCreateSucceeds_responseIdsMatch() {
        final RoleDTO role = new RoleDTO();
        role.setId("the-id");
        role.setPermissions(Collections.emptyList());
        final RoleDTO role2 = new RoleDTO();
        role2.setId("the-id-2");
        role2.setPermissions(Collections.emptyList());


        when(roleServiceClient.createEntity(role, authHeaders)).thenReturn(role);
        when(roleServiceClient.createEntity(role2, authHeaders)).thenReturn(role2);
        final List<String> actual =
                roleServiceInvoker.createRoles(Arrays.asList(role, role2), RoleServiceClient.noAuth());
        //Assert
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0), equalTo(role.getId()));
        assertThat(actual.get(1), equalTo(role2.getId()));
    }


    @Test
    void whenOneServiceError_thenThrowRestException() {
        // Arrange
        RoleDTO role = new RoleDTO();
        List<RoleDTO> entities = Arrays.asList(role, role);
        doThrow(RuntimeException.class).when(roleServiceClient).createEntity(role, authHeaders);
        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> roleServiceInvoker.createRoles(entities, authHeaders));
    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> roleServiceInvoker.createRoles(null, RoleServiceClient.noAuth()));
    }

    @Test
    void testRoleService_WithList_WhenValidInput() {
        RoleDTO testRole = new RoleDTO();
        testRole.setId("testId");

        PermissionDTO testPermission = new PermissionDTO();
        testPermission.setId("testId");

        testRole.setPermissions(Collections.singletonList(testPermission));

        List<RoleDTO> roles = Collections.singletonList(testRole);

        try {
            when(roleServiceClient.createEntity(eq(testRole), any(Consumer.class))).thenReturn(testRole);
            roleServiceInvoker.createRoles(roles, RoleServiceClient.bearerAuth(token));
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
