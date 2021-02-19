package uk.ac.ox.ndph.mts.roleserviceclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.main.allow-bean-definition-overriding=true"})
class GetRolesByIdsTest {

    public static TestServiceBackend webServer;
    private RoleServiceClient sut;
    private static WebClient.Builder builder;

    @BeforeAll
    static void init() {
        final WebClientConfig config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        builder = config.webClientBuilder();

        webServer = TestServiceBackend.autoStart();
    }

    @BeforeEach
    void setUp()  {
        sut = new RoleServiceClient(builder, webServer.getUrl());
    }

    @AfterAll
    static void cleanup() {
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


        String expectedBodyResponse =String.format(
                "[{\"createdDateTime\":\"2021-02-07T17:56:23.837542\",\"createdBy\":\"fake-id\"," +
                        "\"modifiedDateTime\":\"2021-02-07T17:56:23.837542\",\"modifiedBy\":\"fake-id\"," +
                        "\"id\":\"%s\",\"permissions\":[{\"createdDateTime\":null,\"createdBy\":\"test\"," +
                        "\"modifiedDateTime\":null,\"modifiedBy\":\"test\",\"id\":\"some-permission\"}]}]", roleId);

        webServer.queueResponse(expectedBodyResponse);

        // Act
        List<RoleDTO> actualResponse = sut.getRolesByIds(Collections.singletonList(roleId));

        //Assert
        assertAll(
                () -> assertEquals(1 , actualResponse.size()),
                () -> assertEquals(expectedRoleResponse.getId() , actualResponse.get(0).getId()),
                () -> assertEquals(expectedRoleResponse.getPermissions().size() ,
                        actualResponse.get(0).getPermissions().size()),
                () -> assertEquals(expectedRoleResponse.getPermissions().get(0).getId() ,
                        actualResponse.get(0).getPermissions().get(0).getId())
        );
    }

    @Test
    void whenServiceFails_ThrowsRestException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> sut.getRolesByIds(Collections.singletonList("roleId")));
    }

}
