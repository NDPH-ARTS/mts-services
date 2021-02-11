package uk.ac.ox.ndph.mts.sample_service.client.role_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.sample_service.client.TestServiceBackend;
import uk.ac.ox.ndph.mts.sample_service.client.WebClientConfig;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.main.allow-bean-definition-overriding=true"})
class RoleServiceClientImplTests {

    public static TestServiceBackend mockBackEnd;

    private RoleServiceClientImpl client;

    private static WebClient.Builder builder;

    @BeforeAll
    static void init() {
        final WebClientConfig config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        builder = config.webClientBuilder();
    }

    @BeforeEach
    void setUp()  {
        mockBackEnd = TestServiceBackend.autoStart();
        this.client = new RoleServiceClientImpl(builder, mockBackEnd.getUrl());
    }

    @AfterEach
    void cleanup() {
        mockBackEnd.shutdown();
    }

    @Test
    void TestGetRolesById_WithValidResponse_ReturnsRolesWithPermissionsAsExpected() {

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

        mockBackEnd.queueResponse(expectedBodyResponse);

        // Act
        List<RoleDTO> actualResponse = client.getRolesByIds(Collections.singletonList(roleId));

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
    void TestGetRolesById_WhenServiceFails_ThrowsRestException() {
        // Arrange
        mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getRolesByIds(Collections.singletonList("roleId")));
    }

}
