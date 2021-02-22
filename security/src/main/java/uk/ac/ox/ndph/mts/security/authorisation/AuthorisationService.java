package uk.ac.ox.ndph.mts.security.authorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.client.role_service.RoleServiceClient;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.client.dtos.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorisationService.class);

    private final SecurityContextUtil securityContextUtil;

    private final PractitionerServiceClient practitionerServiceClient;
    private final RoleServiceClient roleServiceClient;

    @Value("Managed_Identity")
    private String Managed_Identity;

    @Autowired
    public AuthorisationService(final SecurityContextUtil securityContextUtil,
                                final PractitionerServiceClient practitionerServiceClient,
                                final RoleServiceClient roleServiceClient) {
        this.securityContextUtil = securityContextUtil;
        this.practitionerServiceClient = practitionerServiceClient;
        this.roleServiceClient = roleServiceClient;
    }

    /**
     * Authorise request
     * @return true if authorised
     */
    public boolean authorise(String requiredPermission)  {

        try {
            //Get the user's object id
            String userId = securityContextUtil.getUserId();

            //If user is a service it is authorized.
            if(isManagedServiceIdentity(userId)) {
                return true;
            }

            //get practitioner role assignment
            List<RoleAssignmentDTO> roleAssignments = practitionerServiceClient.getUserRoleAssignments(userId);

            if (roleAssignments == null || roleAssignments.isEmpty()) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            if (!hasValidPermissions(requiredPermission, roleAssignments)) {
                return false;
            }

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed. Error message: %s", e.getMessage()));
            return false;
        }

        //The next step will be to validate the site (ARTS-360). Currently just a stub.
        return isSiteAuthorised("stubSite", "stubRole");
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

    /**
     * Validate user site trees allowed the entity site
     * @param userRole - participant role which includes the site property
     * @param entitySite - the requested entity site
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    @SuppressWarnings("squid:S1172") //suppress unused parameter
    private boolean isSiteAuthorised(String entitySite, String userRole) {
        return true;
    }

    /**
     * Validate if the user matches a system service identity
     * @param userId - the requested userId
     * @return true if user and identity match;
     */
    private boolean isManagedServiceIdentity(String userId) {
        return userId.equals(Managed_Identity);
    }
}
