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
import uk.ac.ox.ndph.mts.sample_service.client.role_service.RoleServiceClientImpl;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;
import uk.ac.ox.ndph.mts.sample_service.security.authroisation.AuthorisationService;
import uk.ac.ox.ndph.mts.sample_service.security.authroisation.SecurityContextService;
import java.util.Collections;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorisationServiceTests {

    @Mock
    private RoleServiceClientImpl roleServiceClientImpl;

    @Mock
    private PractitionerServiceClientImpl practitionerServiceClient;

    @Mock
    private SecurityContextService securityContextService;

    private AuthorisationService authorisationService;

    private UserPrincipal userPrincipal = new UserPrincipal(null,
            (new JWTClaimsSet.Builder()).claim("oid", "123").build()) ;

    @BeforeEach
    void setup() {
        this.authorisationService = new AuthorisationService(securityContextService,
                practitionerServiceClient,
                roleServiceClientImpl);
    }

    @Test
    void TestAuthorise_WithInvalidToken_ReturnsFalse(){
        when(securityContextService.getUserPrincipal()).thenThrow(new AuthorisationException("Any exception during getting user from token"));
        Assertions.assertFalse(authorisationService.authorise("some-permission"));

    }

    @Test
    void TestAuthorise_WithUserThatHasNullAssignmentRoles_ReturnsFalse(){
        when(securityContextService.getUserPrincipal()).thenReturn(userPrincipal);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(null);

        Assertions.assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WithUserThatHasEmptyAssignmentRoles_ReturnsFalse(){
        when(securityContextService.getUserPrincipal()).thenReturn(userPrincipal);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(new RoleAssignmentDTO[0]);

        Assertions.assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingAssignmentRolesThrowsException_ReturnsFalse(){
        when(securityContextService.getUserPrincipal()).thenReturn(userPrincipal);
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenThrow(new RestException("Any exception"));

        Assertions.assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingNoRolesWereFound_ReturnsFalse(){

        when(securityContextService.getUserPrincipal()).thenReturn(userPrincipal);


        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO("siteId", "roleId");
        RoleAssignmentDTO[] roleAssignmentDtos = { roleAssignmentDTO };
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(roleAssignmentDtos);

        when(roleServiceClientImpl.getRolesById(roleAssignmentDTO.getRoleId())).thenReturn(null);

        Assertions.assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleDoesNotContainThePermission_ReturnsFalse(){

        when(securityContextService.getUserPrincipal()).thenReturn(userPrincipal);

        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO("siteId", "roleId");
        RoleAssignmentDTO[] roleAssignmentDtos = { roleAssignmentDTO };
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(roleAssignmentDtos);

        RoleDTO roleDto = new RoleDTO("roleID", Collections.singletonList(new PermissionDTO("another_permission")));
        when(roleServiceClientImpl.getRolesById(roleAssignmentDTO.getRoleId())).thenReturn(roleDto);

        Assertions.assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleWithThePermission_ReturnsTrue(){

        when(securityContextService.getUserPrincipal()).thenReturn(userPrincipal);

        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO("siteId", "roleId");
        RoleAssignmentDTO[] roleAssignmentDtos = { roleAssignmentDTO };
        when(practitionerServiceClient.getUserAssignmentRoles("123")).thenReturn(roleAssignmentDtos);

        RoleDTO roleDto = new RoleDTO("roleID", Collections.singletonList(new PermissionDTO("some_permission")));
        when(roleServiceClientImpl.getRolesById(roleAssignmentDTO.getRoleId())).thenReturn(roleDto);

        Assertions.assertTrue(authorisationService.authorise("some_permission"));
    }

}
