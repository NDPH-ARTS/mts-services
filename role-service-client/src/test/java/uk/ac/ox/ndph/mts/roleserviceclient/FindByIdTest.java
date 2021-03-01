package uk.ac.ox.ndph.mts.roleserviceclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.net.HttpURLConnection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FindByIdTest {

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
    void withValidResponse_ReturnsRolesWithPermissionsAsExpected() {
        // Arrange
        final var roleId = "roleId";

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId("some-permission");
        RoleDTO expectedRoleResponse = new RoleDTO();
        expectedRoleResponse.setId(roleId);
        expectedRoleResponse.setPermissions(Collections.singletonList(permissionDTO));


        String expectedBodyResponse = String.format(
            "{\"createdDateTime\":\"2021-02-07T17:56:23.837542\",\"createdBy\":\"fake-id\"," +
                "\"modifiedDateTime\":\"2021-02-07T17:56:23.837542\",\"modifiedBy\":\"fake-id\"," +
                "\"id\":\"%s\",\"permissions\":[{\"createdDateTime\":null,\"createdBy\":\"test\"," +
                "\"modifiedDateTime\":null,\"modifiedBy\":\"test\",\"id\":\"some-permission\"}]}", roleId);

        webServer.queueResponse(expectedBodyResponse);

        // Act
        final RoleDTO actualResponse =
            roleServiceClient.findById(roleId, RoleServiceClient.noAuth());

        //Assert
        assertAll(
            () -> assertEquals(expectedRoleResponse.getId(), actualResponse.getId()),
            () -> assertEquals(expectedRoleResponse.getPermissions().size(),
                actualResponse.getPermissions().size()),
            () -> assertEquals(expectedRoleResponse.getPermissions().get(0).getId(),
                actualResponse.getPermissions().get(0).getId())
        );
    }

    @Test
    void whenServiceFails_ThrowsRestException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Act + Assert
        assertThrows(RestException.class,
            () -> roleServiceClient.findById("any-role-id", RoleServiceClient.noAuth()));
    }

}
