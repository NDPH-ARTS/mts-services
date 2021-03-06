package uk.ac.ox.ndph.mts.roleserviceclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FindByIdsTest {

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
    void withValidResponse_ReturnsRolesWithPermissionsAsExpected() {
        // Arrange
        final var roleId = "roleId";

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId("some-permission");
        RoleDTO expectedRoleResponse = new RoleDTO();
        expectedRoleResponse.setId(roleId);
        expectedRoleResponse.setPermissions(Collections.singletonList(permissionDTO));


        String expectedBodyResponse = String.format(
            "[{\"createdDateTime\":\"2021-02-07T17:56:23.837542\",\"createdBy\":\"fake-id\"," +
                "\"modifiedDateTime\":\"2021-02-07T17:56:23.837542\",\"modifiedBy\":\"fake-id\"," +
                "\"id\":\"%s\",\"permissions\":[{\"createdDateTime\":null,\"createdBy\":\"test\"," +
                "\"modifiedDateTime\":null,\"modifiedBy\":\"test\",\"id\":\"some-permission\"}]}]", roleId);

        webServer.queueResponse(expectedBodyResponse);

        // Act
        List<RoleDTO> actualResponse =
            roleServiceClient.getRolesByIds(Collections.singletonList(roleId), authHeaders);

        //Assert
        assertAll(
            () -> assertEquals(1, actualResponse.size()),
            () -> assertEquals(expectedRoleResponse.getId(), actualResponse.get(0).getId()),
            () -> assertEquals(expectedRoleResponse.getPermissions().size(),
                actualResponse.get(0).getPermissions().size()),
            () -> assertEquals(expectedRoleResponse.getPermissions().get(0).getId(),
                actualResponse.get(0).getPermissions().get(0).getId())
        );
    }

    @Test
    void whenServiceFails_ThrowsRestException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Act + Assert
        assertThrows(Exception.class,
            () -> roleServiceClient.getRolesByIds(Collections.singletonList("any-role-id"), authHeaders));
    }

}
