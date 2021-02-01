package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Permission;
import uk.ac.ox.ndph.mts.init_service.model.Role;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    RoleServiceInvoker roleServiceInvoker;

    MockWebServer mockBackEnd;

    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.create(String.format("http://localhost:%s",
                mockBackEnd.getPort()));
        roleServiceInvoker = new RoleServiceInvoker(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void testRoleService_WithRoleNoPermissions_WhenValidInput() throws IOException {
        Role testRole = new Role();
        testRole.setId("testId");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testRole))
               .addHeader("Content-Type", "application/json"));
        Role returnedRole = roleServiceInvoker.send(testRole);
        assertNotNull(returnedRole);
    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> roleServiceInvoker.execute(null));
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        Role testRole = new Role();
        testRole.setId("testId");

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));

        List<Role> roles = Collections.singletonList(testRole);
        assertThrows(DependentServiceException.class, () -> roleServiceInvoker.execute(roles));
    }

    @Test
    void testRoleService_WithList_WhenValidInput() throws IOException {
        Role testRole = new Role();
        testRole.setId("testId");

        Permission testPermission = new Permission();
        testPermission.setId("testId");

        testRole.setPermissions(Collections.singletonList(testPermission));

        List<Role> roles = Collections.singletonList(testRole);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testRole))
                .addHeader("Content-Type", "application/json"));
        try {
            roleServiceInvoker.execute(roles);
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
