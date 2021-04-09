package uk.ac.ox.ndph.mts.security.authorisation;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.client.practitioner_service.PractitionerServiceClientImpl;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.security.exception.RestException;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorisationServiceTests {

    @Mock
    private RoleServiceClient roleServiceClient;

    @Mock
    private PractitionerServiceClientImpl practitionerServiceClient;

    @Mock
    private SiteServiceClient siteServiceClient;

    @Mock
    private SecurityContextUtil securityContextUtil;

    private AuthorisationService authorisationService;

    private final String managedIdentity = "999";

    @BeforeEach
    void setup() {
        this.authorisationService = new AuthorisationService(securityContextUtil,
                new SiteUtil(),
                practitionerServiceClient,
                roleServiceClient,
                siteServiceClient);
        ReflectionTestUtils.setField(authorisationService, "managedIdentity", managedIdentity);


    }

    @Test
    void TestBypassAuthorise_WhenUser_ManagedIdentity(){
        //Arrange
        when(securityContextUtil.getUserId()).thenReturn(managedIdentity);
        String userId = managedIdentity;
        when(securityContextUtil.isInIdentityProviderRole()).thenReturn(true);

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
        when(roleServiceClient.getRolesByIds(eq(Collections.singletonList(roleId)), any(Consumer.class))).thenReturn(null);

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
        when(roleServiceClient.getRolesByIds(eq(Collections.singletonList(roleId)), any(Consumer.class))).thenReturn(roleDtos);

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
        when(roleServiceClient.getRolesByIds(eq(Collections.singletonList(roleId)), any(Consumer.class))).thenReturn(roleDtos);

        //Act
        //Assert
        assertTrue(authorisationService.authorise("some_permission"));
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
        when(roleServiceClient.getRolesByIds(eq(Collections.singletonList(roleId)), any(Consumer.class))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);
        when(siteServiceClient.getAllSites(any(Consumer.class))).thenReturn(Collections.singletonList(siteDto));

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
        when(roleServiceClient.getRolesByIds(eq(Collections.singletonList(roleId)), any(Consumer.class))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);
        when(siteServiceClient.getAllSites(any(Consumer.class))).thenReturn(Collections.singletonList(siteDto));

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
        when(roleServiceClient.getRolesByIds(eq(Collections.singletonList(roleId)), any(Consumer.class))).thenReturn(roleDtos);

        var siteDto = new SiteDTO();
        siteDto.setSiteId(authorisedSiteId);
        when(siteServiceClient.getAllSites(SiteServiceClient.bearerAuth(securityContextUtil.getToken()))).thenReturn(Collections.singletonList(siteDto));

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

    @Test
    void TestAuthorise_WithNullSiteIdInList_ReturnsFalse() {

        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        List<String> siteIdsOneNull = Collections.singletonList(null);
        assertFalse(authorisationService.authorise("some_permission", siteIdsOneNull));

        List<String> siteIdsOneNullOneNot = Arrays.asList("site42", null);
        assertFalse(authorisationService.authorise("some_permission", siteIdsOneNullOneNot));
    }

    @Test
    void TestAuthorise_WithNullSiteIdList_ReturnsFalse() {

        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        List<String> siteIds = null;
        assertFalse(authorisationService.authorise("some_permission", siteIds));
    }

    @Test
    void TestFilterMySites_ForUserWithRootRoleAssignment_ReturnsAllSites(){
        //Arrange
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());

        var sitesToFilter = Lists.list(parentSite, childSite1, childSite2);

        var roleAssignments = getRoleAssignments("roleId", parentSite.getSiteId());

        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignments);

        //Act
        var authResponse = authorisationService.filterUserSites(sitesToFilter);

        //Assert
        assertAll(
                () -> assertTrue(authResponse),
                () -> assertEquals(3, sitesToFilter.size()),
                () -> assertTrue(sitesToFilter.contains(parentSite)),
                () -> assertTrue(sitesToFilter.contains(childSite1)),
                () -> assertTrue(sitesToFilter.contains(childSite2))
        );

    }

    @Test
    void TestFilterMySites_ForUserWithRoleAssignment_ReturnsAuthorisedSitesOnly(){
        //Arrange
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite = new SiteDTO("hospital", childSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());

        List<SiteDTO> sitesToFilter = Lists.list(parentSite, childSite1, grandChildSite, childSite2);

        var roleAssignments = getRoleAssignments("roleId", childSite1.getSiteId());

        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(roleAssignments);

        //Act
        var authResponse = authorisationService.filterUserSites(sitesToFilter);

        //Assert
        assertAll(
                () -> assertTrue(authResponse),
                () -> assertEquals(2, sitesToFilter.size()),
                () -> assertTrue(sitesToFilter.contains(childSite1)),
                () -> assertTrue(sitesToFilter.contains(grandChildSite))
        );

    }

    @Test
    void TestFilterMySites_ForUserWithNoRoleAssignment_ReturnsFalse(){
        //Arrange
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite = new SiteDTO("hospital", childSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());

        List<SiteDTO> sitesToFilter = Lists.list(parentSite, childSite1, grandChildSite, childSite2);

        String userId = "123";
        String token = "token";
        when(securityContextUtil.getUserId()).thenReturn(userId);
        when(securityContextUtil.getToken()).thenReturn(token);

        when(practitionerServiceClient.getUserRoleAssignments(userId, token)).thenReturn(Lists.emptyList());

        //Act
        var authResponse = authorisationService.filterUserSites(sitesToFilter);

        //Assert
        assertFalse(authResponse);

    }

    @Test
    void TestAuthoriseUserRoles_WithUserRequestingItsRoles_ReturnsTrue(){
        //Arrange
        String requestingUserId = "123";
        when(securityContextUtil.getUserId()).thenReturn(requestingUserId);

        //Act
        //Assert
        assertTrue(authorisationService.authoriseUserRoles(requestingUserId));
    }

    @Test
    void TestAuthoriseUserRoles_WithUserRequestingRolesForAnotherUSer_ReturnsFalse(){
        //Arrange
        String requestingUserId = "123";
        when(securityContextUtil.getUserId()).thenReturn(requestingUserId);

        String otherUserId = "456";

        //Act
        //Assert
        assertFalse(authorisationService.authoriseUserRoles(otherUserId));
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

        return Collections.singletonList(roleAssignmentDTO);

    }

    private static class TestEntityObject{

        private String siteId;

        public TestEntityObject(String siteId) {
            this.siteId = siteId;
        }

        public String getSiteId() {
            return this.siteId;
        }
    }
}
