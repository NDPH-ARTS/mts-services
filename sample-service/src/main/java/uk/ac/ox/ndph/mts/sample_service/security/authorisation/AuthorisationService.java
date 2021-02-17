package uk.ac.ox.ndph.mts.sample_service.security.authorisation;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.sample_service.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.role_service.RoleServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.ndph.mts.sample_service.client.site_service.SiteServiceClient;
import uk.ac.ox.ndph.mts.sample_service.exception.AuthorisationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public boolean authorise(String requiredPermission, List<Object> sites, String methodName) {
        List<String> siteIds = sites.stream()
                .map(site -> getSiteIdFromObj(site, methodName))
                .collect(Collectors.toList());
        return authorise(requiredPermission, siteIds);
    }

    public boolean authorise(String requiredPermission, String siteId) {
        return authorise(requiredPermission, Collections.singletonList(siteId));
    }

    public boolean authorise(String requiredPermission) {
        return authorise(requiredPermission, Lists.newArrayList());
    }


    /**
     * Authorise request
     * @return true if authorised
     */
    public boolean authorise(String requiredPermission, List<String> entitiesSiteIds)  {

        try {
            //Get the user's object id
            String userId = securityContextUtil.getUserId();

            //get practitioner role assignment
            List<RoleAssignmentDTO> roleAssignments = practitionerServiceClient.getUserRoleAssignments(userId);

            if (roleAssignments == null || roleAssignments.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            if (!hasValidPermissions(requiredPermission, roleAssignments)) {
                return false;
            }

            List<SiteDTO> sites = siteServiceClient.getAllSites();

            return isAuthorisedToAllSites(sites, roleAssignments, entitiesSiteIds);

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
    private boolean hasValidPermissions(String requiredPermission, List<RoleAssignmentDTO> roleAssignments) {

        try {

            List<String> roleIds = roleAssignments.stream()
                    .map(RoleAssignmentDTO::getRoleId).collect(Collectors.toList());

            //get permissions for the the practitioner role assignments
            //and filter role assignments to be only those which have the required permission in them
            var hasNoRoleWithPermission = roleServiceClient.getRolesByIds(roleIds).stream()
                    .filter(roleDto -> hasRequiredPermissionInRole(roleDto, requiredPermission))
                    .findFirst()
                    .isEmpty();

            //validate the required permission is present
            if (hasNoRoleWithPermission) {
                return false;
            }

            return true;

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed on validating user assignment role permissions. "
                    + "Error message: %s", e.getMessage()));
            return false;
        }
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

        Map<String, ArrayList<SiteDTO>> tree = siteTreeUtil.getSiteSubTrees(sites);

        var hasAnUnauthorisedSite = entitiesSiteIds.stream().anyMatch(siteId ->
                roleAssignments.stream()
                        .map(RoleAssignmentDTO::getSiteId).distinct()
                        .noneMatch(roleSiteId ->
                                tree.get(roleSiteId).stream()
                                        .anyMatch(siteDTO -> siteDTO.getSiteId().equals(siteId)))
        );

        return !hasAnUnauthorisedSite;
    }

    private String getSiteIdFromObj(Object obj, String methodName) {
        try {
            Method getSiteMethod = obj.getClass().getMethod(methodName);
            return getSiteMethod.invoke(obj).toString();
        } catch (Exception e) {
            throw new AuthorisationException("Error parsing sites from request body.", e);
        }
    }
}
