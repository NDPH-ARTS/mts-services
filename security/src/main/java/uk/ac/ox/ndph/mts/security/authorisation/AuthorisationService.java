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

    private final SecurityContextUtil securityUtil;
    private final AuthUtil authUtil;

    private final PractitionerServiceClient practServClnt;
    private final RoleServiceClient roleServClnt;
    private final SiteServiceClient siteServClnt;

    @Value("${init-service.identity}")
    private String managedIdentity;

    @Autowired
    public AuthorisationService(final SecurityContextUtil securityUtil,
                                final AuthUtil authUtil,
                                final PractitionerServiceClient practServClnt,
                                final RoleServiceClient roleServClnt,
                                final SiteServiceClient siteServiceClient) {
        this.securityUtil = securityUtil;
        this.authUtil = authUtil;
        this.practServClnt = practServClnt;
        this.roleServClnt = roleServClnt;
        this.siteServClnt = siteServiceClient;
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
     * @param requiredPerm the required permission
     * @param siteIds list of site ids to check if the user is authorised on them
     * @return true if authorised - has the required permission and is authorised on all sites in entitiesSiteIds
     */
    public boolean authorise(String requiredPerm, List<String> siteIds)  {
        try {
            LOGGER.debug("userId Is - {}", securityUtil.getUserId());
            LOGGER.debug("managed identity is - {}", managedIdentity);
            //Managed Service Identities represent a call from a service and is
            //therefore authorized.
            if (securityUtil.isInIdentityProviderRole()) {
                return true;
            }

            // Site IDis should not be null - unless this is init service setting up the root node,
            // but that user is AManagedServiceIdentity
            if (siteIds == null || siteIds.stream().anyMatch(Objects::isNull)) {
                LOGGER.info("SiteID is null therefore request is not unauthorized (permission: {} user: {})",
                        requiredPerm, securityUtil.getUserId());
                return false;
            }

            //get practitioner role assignment
            List<RoleAssignmentDTO> roleAssnmnts =
                practServClnt.getUserRoleAssignments(securityUtil.getUserId(), getAuthHeaders());

            if (roleAssnmnts == null || roleAssnmnts.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.",
                    securityUtil.getUserId());
                return false;
            }

            //Check for the permission in role assignments
            var roleAssnmntsWtPerm = getRoleAssmntsWtPerm(requiredPerm, roleAssnmnts);
            if (roleAssnmntsWtPerm.isEmpty()) {
                return false;
            }

            //If we have required permission at a role and no site given to check against we can return true
            if (siteIds.isEmpty()) {
                return true;
            }

            //Check if the permission exists in roleassignments for siteId
            if (roleAssnmntsWtPerm.stream().anyMatch(ra -> siteIds.get(0).equalsIgnoreCase(ra.getSiteId()))) {
                return true;
            }

            //Check if the permission exists at any ancestors
            List<String> parents = siteServClnt.getParentSiteIds(siteIds.get(0), getAuthHeaders());
            return roleAssnmntsWtPerm.stream().anyMatch(ra -> parents.contains(ra.getSiteId()));

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
    public boolean authUserRoles(String userIdentityParam) {
        String requestUserId = securityUtil.getUserId();
        return requestUserId.equals(userIdentityParam);
    }

    /**
     * Authorise to retrieve only roles the user is assigned to
     * @param ids requested role ids
     * @return true if all role ids are roles that user is assigned to
     */
    public boolean authUserPermRoles(List<String> ids) {
        List<RoleAssignmentDTO> roleAssignments = practServClnt.getUserRoleAssignments(
            securityUtil.getUserId(), getAuthHeaders());

        List<String> roleAssignmentIds = roleAssignments.stream()
                .map(RoleAssignmentDTO::getRoleId).collect(Collectors.toList());

        return roleAssignmentIds.containsAll(ids);
    }

    /**
     * Filter unauthorised sites
     * @param sitesReturnObject all sites returned object
     * @param userRole filter sites by role
     * @param accessPerm filter sites by permission
     * @return true if filtering finished successfully
     */
    public boolean filterUserSites(List<?> sitesReturnObject, String userRole, String accessPerm) {
        try {
            Objects.requireNonNull(sitesReturnObject, "sites can not be null");

            //get unfiltered roleAssignments
            List<RoleAssignmentDTO> roleAssnmts = new ArrayList<>(
                practServClnt.getUserRoleAssignments(securityUtil.getUserId(), getAuthHeaders()));

            //return 403 if RAs is empty
            if (roleAssnmts == null || roleAssnmts.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.",
                    securityUtil.getUserId());
                return false;
            }

            //only keep the assigned sites (or child assigned sites)
            removeUnassignedSites(roleAssnmts, sitesReturnObject);

            //If we dont have receive a role then return the assigned sites
            if (userRole == null) {
                return true;
            }

            //Remove RAs if they dont match the role
            roleAssnmts.removeIf(ra -> !ra.getRoleId().equalsIgnoreCase(userRole));

            //If RAs are empty return 403
            if (roleAssnmts == null || roleAssnmts.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.",
                    securityUtil.getUserId());
                return false;
            }

            //Filter sites by role
            removeUnassignedSites(roleAssnmts, sitesReturnObject);

            //Get the RAs with the given permission - if they are then empty return 403
            List<RoleAssignmentDTO> roleAsstsWtPerm = getRoleAssmntsWtPerm(accessPerm, roleAssnmts);
            if (roleAsstsWtPerm.isEmpty()) {
                return false;
            }

            //Filter sites with perm based RAs
            //check if user has site (or child site) assigned
            removeUnassignedSites(roleAsstsWtPerm, sitesReturnObject);

            //If sites are empty return 403 (false)
            return !sitesReturnObject.isEmpty();
        } catch (Exception e) {
            return false;
        }

    }

    private List<SiteDTO> convertSiteToDTO(List<?> sitesReturnObject) {
        return sitesReturnObject.stream()
            .map(siteObject -> {
                var siteId =  authUtil.getSiteIdFromObj(siteObject, "getSiteId");
                var parentSiteId =  authUtil.getSiteIdFromObj(siteObject, "getParentSiteId");
                return new SiteDTO(siteId, parentSiteId);
            }).collect(Collectors.toList());
    }


    private void removeUnassignedSites(List<RoleAssignmentDTO> roleAssignments, List<?> sitesReturnObject) {
        sitesReturnObject.removeIf(siteObject ->
                !authUtil.getUserSites(convertSiteToDTO(sitesReturnObject), roleAssignments)
                .contains(authUtil.getSiteIdFromObj(siteObject, "getSiteId")));
    }

    /**
     * Validate if the role assignments have the required permission linked to them.
     * @param reqPerm action required permission
     * @param roleAssmnts user role assignments
     * @return true if required permission is present in one of the roles
     */
    private List<RoleAssignmentDTO> getRoleAssmntsWtPerm(String reqPerm,
                                                         List<RoleAssignmentDTO> roleAssmnts) {
        //get permissions for the the practitioner role assignments
        //and filter role assignments to be only those which have the required permission in them
        Page<RoleDTO> roleDTOs = roleServClnt.getPage(0, 500,
            RoleServiceClient.bearerAuth(securityUtil.getToken()));

        Set<String> rolesWithPermission = roleDTOs.stream()
                .filter(roleDto -> hasReqPermInRole(roleDto, reqPerm))
                .map(RoleDTO::getId)
                .collect(Collectors.toSet());

        return roleAssmnts.stream()
                .filter(roleAssignment -> rolesWithPermission.contains(roleAssignment.getRoleId()))
                .collect(Collectors.toList());
    }

    /**
     * Check if a required permission exists in role
     * @param role with permissions
     * @param requiredPermission required permission
     * @return true if permission exists in role
     */
    private boolean hasReqPermInRole(RoleDTO role, String requiredPermission) {
        return role.getPermissions().stream()
                .anyMatch(permission -> permission.getId().equals(requiredPermission));
    }

    private Consumer<org.springframework.http.HttpHeaders> getAuthHeaders() {
        //Get the user's object id
        String tokenString = securityUtil.getToken();
        Consumer<org.springframework.http.HttpHeaders> token = PractitionerServiceClient.bearerAuth(tokenString);

        return token;
    }

}
