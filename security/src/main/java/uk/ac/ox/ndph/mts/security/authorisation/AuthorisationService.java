package uk.ac.ox.ndph.mts.security.authorisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     * Authorise request with list of entities
     * @param requiredPermission the required permission
     * @param requestEntities list of entities in request body
     * @param methodName String representing the method name to retrieve site on each entity in the request
     * @return true if authorised - has the required permission and is authorised on all sites in requestEntities
     */
    public boolean authorise(String requiredPermission, List<?> requestEntities, String methodName) {
        // Iterate over list of objects and get the site id property using the methodName
        // The reason we are using reflection is because we should be able to authorise any object in the system
        // it should have a site id property and should have a method to retrieve it
        List<String> siteIds = requestEntities.stream()
                .map(site -> siteUtil.getSiteIdFromObj(site, methodName))
                .collect(Collectors.toList());
        return authorise(requiredPermission, siteIds);
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
            String token = securityContextUtil.getToken();

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

            List<SiteDTO> sites = siteServiceClient.getAllSites(
                                                    SiteServiceClient.bearerAuth(securityContextUtil.getToken()));

            Set<String> userSites = siteUtil.getUserSites(sites, rolesAssignmentsWithPermission);

            return userSites.containsAll(entitiesSiteIds);

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed. Error message: %s", e.getMessage()));
            return false;
        }
    }

    /**
     * Filter unauthorised sites
     * @param sitesReturnObject all sites returned object
     * @return true if filtering finished successfully
     */
    public boolean filterUserSites(List<?> sitesReturnObject) {

        try {
            Objects.requireNonNull(sitesReturnObject, "sites can not be bull");

            List<SiteDTO> sites = sitesReturnObject.stream()
                    .map(siteObject -> {
                        var siteId =  siteUtil.getSiteIdFromObj(siteObject, "getSiteId");
                        var parentSiteId =  siteUtil.getSiteIdFromObj(siteObject, "getParentSiteId");
                        return new SiteDTO(siteId, parentSiteId);
                    }).collect(Collectors.toList());

            String userId = securityContextUtil.getUserId();
            String token = securityContextUtil.getToken();
            List<RoleAssignmentDTO> roleAssignments = practitionerServiceClient.getUserRoleAssignments(userId, token);
            Set<String> userSites = siteUtil.getUserSites(sites, roleAssignments);

            sitesReturnObject.removeIf(siteObject ->
                    !userSites.contains(siteUtil.getSiteIdFromObj(siteObject, "getSiteId")));

            if (sitesReturnObject.isEmpty()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Validate if the role assignments have the required permission linked to them.
     * @param requiredPermission action required permission
     * @param roleAssignments user role assignments
     * @return true if required permission is present in one of the roles
     */
    private List<RoleAssignmentDTO> getRolesAssignmentsWithPermission(String requiredPermission,
                                                                      List<RoleAssignmentDTO> roleAssignments) {

        List<String> roleIds = roleAssignments.stream()
                .map(RoleAssignmentDTO::getRoleId).collect(Collectors.toList());

        //get permissions for the the practitioner role assignments
        //and filter role assignments to be only those which have the required permission in them
        Set<String> rolesWithPermission = roleServiceClient.getRolesByIds(roleIds, RoleServiceClient.noAuth()).stream()
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

    /**
     * Validate if the user matches a system managed service identity
     * @param userId - the requested userId
     * @return true if user and identity match;
     */
    private boolean isUserAManagedServiceIdentity(String userId) {
        return userId.equals(managedIdentity);
    }
}
