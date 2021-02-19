package uk.ac.ox.ndph.mts.security.authorisation;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.client.practitioner_service.PractitionerServiceClientImpl;
import uk.ac.ox.ndph.mts.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.client.role_service.RoleServiceClientImpl;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.security.exception.RestException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorisationServiceTests {

    @Mock
    private RoleServiceClientImpl roleServiceClient;

    @Mock
    private PractitionerServiceClientImpl practitionerServiceClient;

    @Mock
    private SecurityContextUtil securityContextUtil;

    private AuthorisationService authorisationService;

    @BeforeEach
    void setup() {
        this.authorisationService = new AuthorisationService(securityContextUtil,
                practitionerServiceClient,
                roleServiceClient);
    }

    @Test
    void TestAuthorise_WithInvalidToken_ReturnsFalse(){
        //Arrange
        when(securityContextUtil.getUserId()).thenThrow(new AuthorisationException("Any exception during getting user from token"));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));

    }

    @Test
    void TestAuthorise_WithUserThatHasNullRoleAssignments_ReturnsFalse(){
        //Arrange
        String userId = "123";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(practitionerServiceClient.getUserRoleAssignments(userId)).thenReturn(null);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WithUserThatHasEmptyRoleAssignments_ReturnsFalse(){
        //Arrange
        String userId = "123";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(practitionerServiceClient.getUserRoleAssignments(userId)).thenReturn(Lists.emptyList());

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingRoleAssignmentsThrowsException_ReturnsFalse(){
        //Arrange
        String userId = "123";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(practitionerServiceClient.getUserRoleAssignments(userId)).thenThrow(new RestException("Any exception"));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingNoRolesWereFound_ReturnsFalse(){
        //Arrange
        String userId = "123";
        when(securityContextUtil.getUserId()).thenReturn(userId);

        String roleId = "roleId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId);

        when(practitionerServiceClient.getUserRoleAssignments(userId)).thenReturn(roleAssignmentDtos);
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(null);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleDoesNotContainThePermission_ReturnsFalse(){
        //Arrange
        String userId = "123";
        when(securityContextUtil.getUserId()).thenReturn(userId);

        String roleId = "roleId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId);
        when(practitionerServiceClient.getUserRoleAssignments(userId)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "another_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleWithThePermission_ReturnsTrue(){
        //Arrange
        String userId = "123";
        when(securityContextUtil.getUserId()).thenReturn(userId);

        String roleId = "roleId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId);
        when(practitionerServiceClient.getUserRoleAssignments(userId)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        //Act
        //Assert
        Assertions.assertTrue(authorisationService.authorise("some_permission"));
    }

    private RoleDTO getRoleWithPermissions(String roleId, String permission){
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(permission);
        RoleDTO roleDto = new RoleDTO();
        roleDto.setId(roleId);
        roleDto.setPermissions(Collections.singletonList(permissionDTO));

        return roleDto;
    }

    private List<RoleAssignmentDTO> getRoleAssignments(String roleId){
        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO();
        roleAssignmentDTO.setRoleId("roleId");
        roleAssignmentDTO.setSiteId("siteId");

        List<RoleAssignmentDTO> roleAssignmentDTOS = Collections.singletonList(roleAssignmentDTO);

        return roleAssignmentDTOS;
    }
}
