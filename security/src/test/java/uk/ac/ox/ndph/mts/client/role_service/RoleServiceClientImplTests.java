//package uk.ac.ox.ndph.mts.client.role_service;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.web.reactive.function.client.WebClient;
//import uk.ac.ox.ndph.mts.client.TestServiceBackend;
//import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
//import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
//import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
//import uk.ac.ox.ndph.mts.security.exception.RestException;
//import java.net.HttpURLConnection;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ContextConfiguration
//class RoleServiceClientImplTests {
//
//    public static TestServiceBackend mockBackEnd;
//
//    private RoleServiceClient client;
//
//    private static WebClient.Builder builder;
//
//    @BeforeAll
//    static void init() {
//        builder = WebClient.builder();
//    }
//
//    @BeforeEach
//    void setUp()  {
//        mockBackEnd = TestServiceBackend.autoStart();
//        this.client = new RoleServiceClient(builder, mockBackEnd.getUrl());
//    }
//
//    @AfterEach
//    void cleanup() {
//        mockBackEnd.shutdown();
//    }
//
//    @Test
//    void TestGetRolesById_WithValidResponse_ReturnsRolesWithPermissionsAsExpected() {
//
//        // Arrange
//
//        final var roleId = "roleId";
//
//        PermissionDTO permissionDTO = new PermissionDTO();
//        permissionDTO.setId("some-permission");
//        RoleDTO expectedRoleResponse = new RoleDTO();
//        expectedRoleResponse.setId(roleId);
//        expectedRoleResponse.setPermissions(Collections.singletonList(permissionDTO));
//
//
//        String expectedBodyResponse =String.format(
//                "[{\"createdDateTime\":\"2021-02-07T17:56:23.837542\",\"createdBy\":\"fake-id\"," +
//                        "\"modifiedDateTime\":\"2021-02-07T17:56:23.837542\",\"modifiedBy\":\"fake-id\"," +
//                        "\"id\":\"%s\",\"permissions\":[{\"createdDateTime\":null,\"createdBy\":\"test\"," +
//                        "\"modifiedDateTime\":null,\"modifiedBy\":\"test\",\"id\":\"some-permission\"}]}]", roleId);
//
//        mockBackEnd.queueResponse(expectedBodyResponse);
//
//        // Act
//        List<uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO> actualResponse = client.getRolesByIds(Collections.singletonList(roleId));
//
//        //Assert
//        assertAll(
//                () -> assertEquals(1 , actualResponse.size()),
//                () -> assertEquals(expectedRoleResponse.getId() , actualResponse.get(0).getId()),
//                () -> assertEquals(expectedRoleResponse.getPermissions().size() ,
//                        actualResponse.get(0).getPermissions().size()),
//                () -> assertEquals(expectedRoleResponse.getPermissions().get(0).getId() ,
//                        actualResponse.get(0).getPermissions().get(0).getId())
//        );
//    }
//
//    @Test
//    void TestGetRolesById_WhenServiceFails_ThrowsRestException() {
//        // Arrange
//        mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
//        // Act
//        // Assert
//        assertThrows(RestException.class, () -> client.getRolesByIds(Collections.singletonList("roleId")));
//    }
//
//}
