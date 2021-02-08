package uk.ac.ox.ndph.mts.sample_service.security.authorisation;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.sample_service.client.practitioner_service.PractitionerServiceClientImpl;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.AssignmentRoleDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.sample_service.client.role_service.RoleServiceClientImpl;
import uk.ac.ox.ndph.mts.sample_service.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;
import uk.ac.ox.ndph.mts.sample_service.security.authroisation.AuthorisationService;
import uk.ac.ox.ndph.mts.sample_service.security.authroisation.SecurityContextComponent;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorisationServiceTests {

    @Mock
    private RoleServiceClientImpl roleServiceClient;

    @Mock
    private PractitionerServiceClientImpl practitionerServiceClient;

    @Mock
    private SecurityContextComponent securityContextComponent;

    private AuthorisationService authorisationService;

    private UserPrincipal userPrincipal = new UserPrincipal(null,
            (new JWTClaimsSet.Builder()).claim("oid", "123").build()) ;

    @BeforeEach
    void setup() {
        this.authorisationService = new AuthorisationService(securityContextComponent,
                practitionerServiceClient,
                roleServiceClient);
    }

    @Test
    void TestAuthorise_WithInvalidToken_ReturnsFalse(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenThrow(new AuthorisationException("Any exception during getting user from token"));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));

    }

    @Test
    void TestAuthorise_WithUserThatHasNullAssignmentRoles_ReturnsFalse(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenReturn(userPrincipal);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(null);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WithUserThatHasEmptyAssignmentRoles_ReturnsFalse(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenReturn(userPrincipal);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(new AssignmentRoleDTO[0]);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingAssignmentRolesThrowsException_ReturnsFalse(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenReturn(userPrincipal);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenThrow(new RestException("Any exception"));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingNoRolesWereFound_ReturnsFalse(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenReturn(userPrincipal);

        String roleId = "roleId";
        AssignmentRoleDTO[] assignmentRoleDtos = getRoleAssignments(roleId);

        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(assignmentRoleDtos);
        when(roleServiceClient.getRolesById(roleId)).thenReturn(null);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleDoesNotContainThePermission_ReturnsFalse(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenReturn(userPrincipal);

        String roleId = "roleId";
        AssignmentRoleDTO[] assignmentRoleDtos = getRoleAssignments(roleId);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(assignmentRoleDtos);

        RoleDTO roleDto = getRoleWithPermissions(roleId, "another_permission");
        when(roleServiceClient.getRolesById(roleId)).thenReturn(roleDto);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleWithThePermission_ReturnsTrue(){
        //Arrange
        when(securityContextComponent.getUserPrincipal()).thenReturn(userPrincipal);

        String roleId = "roleId";
        AssignmentRoleDTO[] assignmentRoleDtos = getRoleAssignments(roleId);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(assignmentRoleDtos);

        RoleDTO roleDto = getRoleWithPermissions(roleId, "some_permission");
        when(roleServiceClient.getRolesById(roleId)).thenReturn(roleDto);

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

    private AssignmentRoleDTO[] getRoleAssignments(String roleId){
        AssignmentRoleDTO assignmentRoleDTO = new AssignmentRoleDTO();
        assignmentRoleDTO.setRoleId("roleId");
        assignmentRoleDTO.setSiteId("siteId");

        AssignmentRoleDTO[] assignmentRoleDTOS = {assignmentRoleDTO};

        return assignmentRoleDTOS;
    }
}
