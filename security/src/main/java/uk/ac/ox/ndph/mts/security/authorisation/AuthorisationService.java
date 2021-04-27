package uk.ac.ox.ndph.mts.security.authorisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.practitionerserviceclient.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorisationService.class);

    private final SecurityContextUtil securityContextUtil;
    private final SiteUtil siteUtil;

    private final PractitionerServiceClient practitionerServiceClient;
    private final RoleServiceClient roleServiceClient;
    private final SiteServiceClient siteServiceClient;

    @Value("${init-service.identity}")
    private String managedIdentity;

    @Autowired
    public AuthorisationService(final SecurityContextUtil securityContextUtil,
                                final SiteUtil siteUtil,
                                final PractitionerServiceClient practitionerServiceClient,
                                final RoleServiceClient roleServiceClient,
                                final SiteServiceClient siteServiceClient) {
        this.securityContextUtil = securityContextUtil;
        this.siteUtil = siteUtil;
        this.practitionerServiceClient = practitionerServiceClient;
        this.roleServiceClient = roleServiceClient;
        this.siteServiceClient = siteServiceClient;
    }

    /**
     * Authorise request with site
     * @param requiredPermission the required permission
     * @param siteId site id the request is performed on
     * @return true if authorised - has the required permission and is authorised on the site
     */
    public boolean authorise(String requiredPermission, String siteId) {
        return authorise(requiredPermission, Collections.singletonList(siteId));
    }

    /**
     * Authorise request
     * @param requiredPermission the required permission
     * @return true if authorised - has the required permission
     */
    public boolean authorise(String requiredPermission) {
        return authorise(requiredPermission, Collections.emptyList());
    }


    /**
     * Authorise request with list of sites
     * @param requiredPermission the required permission
     * @param entitiesSiteIds list of site ids to check if the user is authorised on them
     * @return true if authorised - has the required permission and is authorised on all sites in entitiesSiteIds
     */
    public boolean authorise(String requiredPermission, List<String> entitiesSiteIds)  {


        try {
            //Get the user's object id
            String userId = securityContextUtil.getUserId();
            String tokenString = securityContextUtil.getToken();
            Consumer<org.springframework.http.HttpHeaders> token = PractitionerServiceClient.bearerAuth(tokenString);

            LOGGER.debug("userId Is - {}", userId);
            LOGGER.debug("managed identity is - {}", managedIdentity);

            //Managed Service Identities represent a call from a service and is
            //therefore authorized.
            if (securityContextUtil.isInIdentityProviderRole()) {
                return true;
            }

            // Site IDis should not be null - unless this is init service setting up the root node,
            // but that user is AManagedServiceIdentity
            if (entitiesSiteIds == null || entitiesSiteIds.stream().anyMatch(Objects::isNull)) {
                LOGGER.info("SiteID is null therefore request is not unauthorized (permission: {} user: {})",
                        requiredPermission, userId);
                return false;
            }

            //get practitioner role assignment
            List<RoleAssignmentDTO> roleAssignments = practitionerServiceClient.getUserRoleAssignments(userId, token);

            if (roleAssignments == null || roleAssignments.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            var rolesAssignmentsWithPermission = getRolesAssignmentsWithPermission(requiredPermission, roleAssignments);

            if (rolesAssignmentsWithPermission.isEmpty()) {
                return false;
            }

            List<SiteDTO> sites = siteServiceClient.getAssignedSites(
                                                    SiteServiceClient.bearerAuth(securityContextUtil.getToken()));

            Set<String> userSites = siteUtil.getUserSites(sites, rolesAssignmentsWithPermission);

            return userSites.containsAll(entitiesSiteIds);

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed. Error message: %s", e.getMessage()));
            return false;
        }
    }

    /**
     * Authorise request with list of sites
     * @param requiredPermission the required permission
     * @return true if authorised - has the required permission and is authorised on all sites in entitiesSiteIds
     */
    public boolean authoriseSites(String requiredPermission)  {


        try {
            //Get the user's object id
            String userId = securityContextUtil.getUserId();
            String tokenString = securityContextUtil.getToken();
            Consumer<org.springframework.http.HttpHeaders> token = PractitionerServiceClient.bearerAuth(tokenString);

            LOGGER.debug("userId Is - {}", userId);
            LOGGER.debug("managed identity is - {}", managedIdentity);

            //Managed Service Identities represent a call from a service and is
            //therefore authorized.
            if (securityContextUtil.isInIdentityProviderRole()) {
                return true;
            }

            //get practitioner role assignment
            List<RoleAssignmentDTO> roleAssignments = practitionerServiceClient.getUserRoleAssignments(userId, token);

            if (roleAssignments == null || roleAssignments.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            var rolesAssignmentsWithPermission = getRolesAssignmentsWithPermission(requiredPermission, roleAssignments);

            if (rolesAssignmentsWithPermission.isEmpty()) {
                return false;
            }

            return true;

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed. Error message: %s", e.getMessage()));
            return false;
        }
    }

    /**
     * Authorise user to retrieve data only for itself
     * @param userIdentityParam the user identity parameter
     * @return true if userIdentityParam equals to the requesting user
     */
    public boolean authoriseUserRoles(String userIdentityParam) {
        String requestUserId = securityContextUtil.getUserId();
        return requestUserId.equals(userIdentityParam);
    }

    /**
     * Authorise to retrieve only roles the user is assigned to
     * @param ids requested role ids
     * @return true if all role ids are roles that user is assigned to
     */
    public boolean authoriseUserPermissionRoles(List<String> ids) {
        String userId = securityContextUtil.getUserId();
        String token = securityContextUtil.getToken();

        List<RoleAssignmentDTO> roleAssignments =
                practitionerServiceClient.getUserRoleAssignments(userId, PractitionerServiceClient.bearerAuth(token));

        List<String> roleAssignmentIds = roleAssignments.stream()
                .map(RoleAssignmentDTO::getRoleId).collect(Collectors.toList());

        return roleAssignmentIds.containsAll(ids);
    }

    /**
     * Filter unauthorised sites
     * @param sitesReturnObject all sites returned object
     * @param userRole filter sites by role
     * @param accessPermission filter sites by permission
     * @return true if filtering finished successfully
     */
    public boolean filterUserSites(List<?> sitesReturnObject, String userRole, String accessPermission) {

        try {
            Objects.requireNonNull(sitesReturnObject, "sites can not be null");

            //get user info
            String userId = securityContextUtil.getUserId();
            String token = securityContextUtil.getToken();
            Consumer<org.springframework.http.HttpHeaders> authHeaders = PractitionerServiceClient.bearerAuth(token);

            //convert sites to siteDTOs
            List<SiteDTO> sites = convertSiteToDTO(sitesReturnObject);

            //get unfiltered roleAssignments
            List<RoleAssignmentDTO> roleAssignments =
                new ArrayList<>(practitionerServiceClient.getUserRoleAssignments(userId, authHeaders));

            //return 403 if RAs is empty
            if (roleAssignments == null || roleAssignments.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            //only keep the assigned sites (or child assigned sites)
            removeUnassignedSites(sites, roleAssignments, sitesReturnObject);

            //If we dont have receive a role then return the assigned sites
            if (userRole == null) {
                return true;
            }

            //Remove RAs if they dont match the role
            roleAssignments.removeIf(ra -> !ra.getRoleId().equalsIgnoreCase(userRole));

            //If RAs are empty return 403
            if (roleAssignments == null || roleAssignments.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            //Filter sites with role based RA
            removeUnassignedSites(sites, roleAssignments, sitesReturnObject);

            //Filter the RAs on the permission - if they are then empty return 403
            var rolesAssignmentsWithPermission = getRolesAssignmentsWithPermission(accessPermission, roleAssignments);
            if (rolesAssignmentsWithPermission.isEmpty()) {
                return false;
            }

            //Filter sites with role+perm based RAs
            //check if user has site (or child site) assigned
            removeUnassignedSites(sites, rolesAssignmentsWithPermission, sitesReturnObject);

            //If sites are empty return 403 (false)
            return !sitesReturnObject.isEmpty();
        } catch (Exception e) {
            return false;
        }

    }

    private List<SiteDTO> convertSiteToDTO(List<?> sitesReturnObject) {
        return sitesReturnObject.stream()
            .map(siteObject -> {
                var siteId =  siteUtil.getSiteIdFromObj(siteObject, "getSiteId");
                var parentSiteId =  siteUtil.getSiteIdFromObj(siteObject, "getParentSiteId");
                return new SiteDTO(siteId, parentSiteId);
            }).collect(Collectors.toList());
    }


    private void removeUnassignedSites(
        List<SiteDTO> sites, List<RoleAssignmentDTO> roleAssignments, List<?> sitesReturnObject) {
        sitesReturnObject.removeIf(siteObject ->
                !siteUtil.getUserSites(sites, roleAssignments)
                .contains(siteUtil.getSiteIdFromObj(siteObject, "getSiteId")));
    }

    /**
     * Validate if the role assignments have the required permission linked to them.
     * @param requiredPermission action required permission
     * @param roleAssignments user role assignments
     * @return true if required permission is present in one of the roles
     */
    private List<RoleAssignmentDTO> getRolesAssignmentsWithPermission(String requiredPermission,
                                                                      List<RoleAssignmentDTO> roleAssignments) {

        //get permissions for the the practitioner role assignments
        //and filter role assignments to be only those which have the required permission in them

        Page<RoleDTO> roleDTOs = roleServiceClient.getPage(0, 500,
            RoleServiceClient.bearerAuth(securityContextUtil.getToken()));

        Set<String> rolesWithPermission = roleDTOs.stream()
                .filter(roleDto -> hasRequiredPermissionInRole(roleDto, requiredPermission))
                .map(RoleDTO::getId)
                .collect(Collectors.toSet());

        return roleAssignments.stream()
                .filter(roleAssignment -> rolesWithPermission.contains(roleAssignment.getRoleId()))
                .collect(Collectors.toList());
    }

    /**
     * Check if a required permission exists in role
     * @param role with permissions
     * @param requiredPermission required permission
     * @return true if permission exists in role
     */
    private boolean hasRequiredPermissionInRole(RoleDTO role, String requiredPermission) {
        return role.getPermissions().stream()
                .anyMatch(permission -> permission.getId().equals(requiredPermission));
    }

}
