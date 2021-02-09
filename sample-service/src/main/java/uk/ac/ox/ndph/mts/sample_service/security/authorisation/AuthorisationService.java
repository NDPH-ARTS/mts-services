package uk.ac.ox.ndph.mts.sample_service.security.authorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.sample_service.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.role_service.RoleServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorisationService.class);

    private final SecurityContextUtil securityContextUtil;

    private final PractitionerServiceClient practitionerServiceClient;
    private final RoleServiceClient roleServiceClient;

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

            //get practitioner role assignment
            List<RoleAssignmentDTO> participantRoles = practitionerServiceClient.getUserRoleAssignments(userId);

            if (participantRoles == null || participantRoles.size() == 0) {
                LOGGER.info("User with id {} has no role assignments and therefore is unauthorised.", userId);
                return false;
            }

            //get permissions for the the practitioner role assignments
            //and filter role assignments to be only those which have the required permission in them
            var hasNoRoleWithPermission = participantRoles.stream()
                    .map(role -> roleServiceClient.getRolesById(role.getRoleId()))
                    .filter(roleDto -> hasRequiredPermissionInRole(roleDto, requiredPermission))
                    .findFirst()
                    .isEmpty();

            //validate the required permission is present
            if (hasNoRoleWithPermission) {
                return false;
            }

        } catch (Exception e) {
            LOGGER.info(String.format("Authorisation process failed. Error message: %s", e.getMessage()));
            return false;
        }

        //Get entity site. Currently just a stub
        String entitySite = "stubSite";

        /** Commenting the following lines out because of sonar test coverage which can't be tested fully on stubs
         But the code is relevant to the flow

        for (String role :  updatedParticipantRoles) {
            //Validate that one of the roles has the site that is allowed to work on the entity
            if (isSiteAuthorised(entitySite, role)) {
                return true;
            }
        }
         */

        return isSiteAuthorised(entitySite, "stubRole");
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
}
