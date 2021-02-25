package uk.ac.ox.ndph.mts.security.authorisation;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.ox.ndph.mts.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.client.practitioner_service.PractitionerServiceClientImpl;
import uk.ac.ox.ndph.mts.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.client.role_service.RoleServiceClientImpl;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.client.site_service.SiteServiceClientImpl;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.security.exception.RestException;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorisationServiceTests {

    @Mock
    private RoleServiceClientImpl roleServiceClient;

    @Mock
    private PractitionerServiceClientImpl practitionerServiceClient;

    @Mock
    private SiteServiceClientImpl siteServiceClient;

    @Mock
    private SecurityContextUtil securityContextUtil;

    private SiteTreeUtil siteTreeUtil;

    private AuthorisationService authorisationService;

    private final String managedIdentity = "999";

    @BeforeEach
    void setup() {
        this.authorisationService = new AuthorisationService(securityContextUtil,
                new SiteTreeUtil(),
                practitionerServiceClient,
                roleServiceClient,
                siteServiceClient);
        ReflectionTestUtils.setField(authorisationService, "managedIdentity", managedIdentity);


    }

    @Test
    void TestBypassAuthorise_WhenUser_ManagedIdentity(){
        //Arrange
        String userId = managedIdentity;
        when(securityContextUtil.getUserId()).thenReturn(userId);

        //Act
        //Assert
        assertTrue(authorisationService.authorise("some-permission"));
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
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(null);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WithUserThatHasEmptyRoleAssignments_ReturnsFalse(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(Lists.emptyList());

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingRoleAssignmentsThrowsException_ReturnsFalse(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenThrow(new RestException("Any exception"));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenGettingNoRolesWereFound_ReturnsFalse(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, "siteId");

        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(null);

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some-permission"));
    }

    @Test
    void TestAuthorise_WhenFoundRoleDoesNotContainThePermission_ReturnsFalse(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, "siteId");
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

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
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, "siteId");
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        //Act
        //Assert
        assertTrue(authorisationService.authorise("some_permission"));
    }

    @Test
    void TestAuthorise_WithUnauthorisedSite_ReturnsFalse(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        String authorisedSiteId = "siteId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, authorisedSiteId);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);

        var unauthorisedSiteDto = new SiteDTO();
        unauthorisedSiteDto.setSiteId("unauthorizedSiteId");
        when(siteServiceClient.getAllSites()).thenReturn(List.of(siteDto, unauthorisedSiteDto));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some_permission", "unauthorizedSiteId" ));
    }

    @Test
    void TestAuthorise_WithUnauthorisedSiteInList_ReturnsFalse(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        String authorisedSiteId = "siteId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, authorisedSiteId);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);

        String unauthorizedSiteId = "unauthorizedSiteId";
        var unauthorisedSiteDto = new SiteDTO();
        unauthorisedSiteDto.setSiteId(unauthorizedSiteId);

        when(siteServiceClient.getAllSites()).thenReturn(List.of(siteDto, unauthorisedSiteDto));

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some_permission", List.of(authorisedSiteId, unauthorizedSiteId )));
    }

    @Test
    void TestAuthorise_WithAllAuthorisedSiteInList_ReturnsTrue(){
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        String authorisedSiteId = "siteId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, authorisedSiteId);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);
        when(siteServiceClient.getAllSites()).thenReturn(Collections.singletonList(siteDto));

        //Act
        //Assert
        assertTrue(authorisationService.authorise("some_permission", List.of(authorisedSiteId)));
    }


    @Test
    void TestAuthorise_WithAuthorisedListOfEntitiesObjects_ReturnTrue() {
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        String authorisedSiteId = "siteId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, authorisedSiteId);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);
        when(siteServiceClient.getAllSites()).thenReturn(Collections.singletonList(siteDto));

        List<Object> entitiesList = Collections.singletonList(new TestEntityObject("siteId"));
        String getSiteIdMethodName = "getSiteId";

        //Act
        //Assert
        assertTrue(authorisationService.authorise("some_permission", entitiesList, getSiteIdMethodName));
    }

    @Test
    void TestAuthorise_WithUnauthorisedListOfEntitiesObjects_ReturnsFalse() {
        //Arrange
        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        String roleId = "roleId";
        String authorisedSiteId = "siteId";
        List<RoleAssignmentDTO> roleAssignmentDtos = getRoleAssignments(roleId, authorisedSiteId);
        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignmentDtos);

        List<RoleDTO> roleDtos = Collections.singletonList(getRoleWithPermissions(roleId,
                "some_permission"));
        when(roleServiceClient.getRolesByIds(Collections.singletonList(roleId))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);
        when(siteServiceClient.getAllSites()).thenReturn(Collections.singletonList(siteDto));

        List<Object> entitiesList = List.of(new TestEntityObject("siteId"),
                new TestEntityObject("unauthorisedSiteId"));
        String getSiteIdMethodName = "getSiteId";

        //Act
        //Assert
        assertFalse(authorisationService.authorise("some_permission", entitiesList, getSiteIdMethodName));
    }

    @Test
    void TestAuthorise_WithListOfEntitiesObjectsAndInvalidMethod_ThrowsException() {
        //Arrange
        List<Object> entitiesList = Collections.singletonList(new TestEntityObject("siteId"));
        String getSiteIdMethodName = "nonExistingMethod";

        //Act
        //Assert
        assertThrows(AuthorisationException.class, () -> authorisationService.authorise("some_permission", entitiesList, getSiteIdMethodName));
    }

    private RoleDTO getRoleWithPermissions(String roleId, String permission){
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(permission);
        RoleDTO roleDto = new RoleDTO();
        roleDto.setId(roleId);
        roleDto.setPermissions(Collections.singletonList(permissionDTO));

        return roleDto;
    }

    private List<RoleAssignmentDTO> getRoleAssignments(String roleId, String siteId){
        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO();
        roleAssignmentDTO.setRoleId(roleId);
        roleAssignmentDTO.setSiteId(siteId);

        List<RoleAssignmentDTO> roleAssignmentDTOS = Collections.singletonList(roleAssignmentDTO);

        return roleAssignmentDTOS;
    }

    private class TestEntityObject{

        private String siteId;

        public TestEntityObject(String siteId) {
            this.siteId = siteId;
        }

        public String getSiteId() {
            return this.siteId;
        }
    }
}
