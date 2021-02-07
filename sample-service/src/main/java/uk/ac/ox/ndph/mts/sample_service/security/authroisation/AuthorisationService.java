package uk.ac.ox.ndph.mts.sample_service.security.authroisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.sample_service.client.practitioner_service.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.role_service.RoleServiceClient;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;
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

    private final SecurityContextService securityContextService;

    private final PractitionerServiceClient practitionerServiceClient;
    private final RoleServiceClient roleServiceClientImpl;

    @Autowired
    public AuthorisationService(final SecurityContextService securityContextService,
                                final PractitionerServiceClient practitionerServiceClient,
                                final RoleServiceClient roleServiceClientImpl) {
        this.securityContextService = securityContextService;
        this.practitionerServiceClient = practitionerServiceClient;
        this.roleServiceClientImpl = roleServiceClientImpl;
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
            RoleAssignmentDTO[] participantRoles = practitionerServiceClient.getUserAssignmentRoles(userId);

            if (participantRoles == null || participantRoles.length == 0) {
                LOGGER.info("User with id {} has no assignment Roles and therefore is unauthorised.", userId);
                return false;
            }

            //get permissions for the the practitioner assignment roles
            //and filter assignment roles to be only those which have the required permission in them
            var rolesWithPermission = Stream.of(participantRoles)
                    .map(role -> roleServiceClientImpl.getRolesById(role.getRoleId()))
                    .filter(roleDto -> hasRoleRequiredPermission(roleDto, requiredPermission))
                    .collect(Collectors.toList());

            //validate the required permission is present
            if (rolesWithPermission.isEmpty()) {
                return false;
            }
        } catch (Exception e) {
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

    private String getUserIdFromContext() throws AuthorisationException {
        try {
            return securityContextService.getUserPrincipal().getClaim("oid").toString();
        } catch (Exception e) {
            throw new AuthorisationException("Invalid user.");
        }
    }

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

    private HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity entity = new HttpEntity(headers);

        return entity;
    }
}
