package uk.ac.ox.ndph.mts.security.authorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;

import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.client.role_service.RoleServiceClient;
import uk.ac.ox.ndph.mts.client.site_service.SiteServiceClient;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorisationService.class);

    private final SecurityContextUtil securityContextUtil;
    private final SiteTreeUtil siteTreeUtil;

    private final PractitionerServiceClient practitionerServiceClient;
    private final RoleServiceClient roleServiceClient;
    private final SiteServiceClient siteServiceClient;

    @Value("${init-service.identity}")
    private String managedIdentity;

    @Autowired
    public AuthorisationService(final SecurityContextUtil securityContextUtil,
                                final SiteTreeUtil siteTreeUtil,
                                final PractitionerServiceClient practitionerServiceClient,
                                final RoleServiceClient roleServiceClient,
                                final SiteServiceClient siteServiceClient) {
        this.securityContextUtil = securityContextUtil;
        this.siteTreeUtil = siteTreeUtil;
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
                .map(site -> getSiteIdFromObj(site, methodName))
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

            LOGGER.debug("userId is - " + userId);
            LOGGER.debug("managed identity is - " + managedIdentity);

            //Managed Service Identities represent a call from a service and is
            //therefore authorized.
            if (isUserAManagedServiceIdentity(userId)) {
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

            List<SiteDTO> sites = siteServiceClient.getAllSites();

            return isAuthorisedToAllSites(sites, rolesAssignmentsWithPermission, entitiesSiteIds);

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed. Error message: %s", e.getMessage()));
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
        Set<String> rolesWithPermission = roleServiceClient.getRolesByIds(roleIds).stream()
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

    private boolean isAuthorisedToAllSites(List<SiteDTO> sites,
                                           List<RoleAssignmentDTO> roleAssignments,
                                           List<String> entitiesSiteIds) {

        Map<String, ArrayList<String>> tree = siteTreeUtil.getSiteSubTrees(sites);

        Set<String> allSitesInRoles = roleAssignments.stream()
                .flatMap(roleAssignmentDTO ->
                        tree.getOrDefault(roleAssignmentDTO.getSiteId(), new ArrayList<>()).stream())
                .collect(Collectors.toSet());

        return allSitesInRoles.containsAll(entitiesSiteIds);
    }

    private String getSiteIdFromObj(Object obj, String methodName) {
        try {
            Method getSiteMethod = obj.getClass().getMethod(methodName);
            return getSiteMethod.invoke(obj).toString();
        } catch (Exception e) {
            throw new AuthorisationException("Error parsing sites from request body.", e);
        }
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
