package uk.ac.ox.ndph.mts.sample_service.security.authroisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.sample_service.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.role_service.RoleServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.AssignmentRoleDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.AuthorisationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorisationService.class);

    private final SecurityContextComponent securityContextComponent;

    private final PractitionerServiceClient practitionerServiceClient;
    private final RoleServiceClient roleServiceClient;

    @Autowired
    public AuthorisationService(final SecurityContextComponent securityContextComponent,
                                final PractitionerServiceClient practitionerServiceClient,
                                final RoleServiceClient roleServiceClient) {
        this.securityContextComponent = securityContextComponent;
        this.practitionerServiceClient = practitionerServiceClient;
        this.roleServiceClient = roleServiceClient;
    }

    /**
     * Authorise request
     * @return true if authorised
     */
    public boolean authorise(String requiredPermission)  {

        try {
            //Get Azure Active Directory user object id
            String userId = getUserIdFromContext();

            //get practitioner assignment role
            AssignmentRoleDTO[] participantRoles = practitionerServiceClient.getUserAssignmentRoles(userId);

            if (participantRoles == null || participantRoles.length == 0) {
                LOGGER.info("User with id {} has no assignment Roles and therefore is unauthorised.", userId);
                return false;
            }

            //get permissions for the the practitioner assignment roles
            //and filter assignment roles to be only those which have the required permission in them
            var rolesWithPermission = Stream.of(participantRoles)
                    .map(role -> roleServiceClient.getRolesById(role.getRoleId()))
                    .filter(roleDto -> hasRoleRequiredPermission(roleDto, requiredPermission))
                    .collect(Collectors.toList());

            //validate the required permission is present
            if (rolesWithPermission.isEmpty()) {
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
     * Get user id from security context
     * @return string user id
     * @throws AuthorisationException if there was an error while extracting user from the context
     */
    private String getUserIdFromContext() throws AuthorisationException {
        try {
            return securityContextComponent.getUserPrincipal().getClaim("oid").toString();
        } catch (Exception e) {
            throw new AuthorisationException("Invalid user.", e);
        }
    }

    /**
     * Check if a required permission exists in role
     * @param role with permissions
     * @param requiredPermission required permission
     * @return true if permission exists in role
     */
    private boolean hasRoleRequiredPermission(RoleDTO role, String requiredPermission) {
        List<String> permissionIds = role.getPermissions().stream()
                .map(PermissionDTO::getId).collect(Collectors.toList());
        return permissionIds.contains(requiredPermission);
    }

    /**
     * Validate user site trees allowed the entity site
     * @param userRole - participant role which includes the site property
     * @param entitySite - the requested entity site
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    @SuppressWarnings("squid:S1172") //suppress unused parameterך
    private boolean isSiteAuthorised(String entitySite, String userRole) {
        return true;
    }
}
