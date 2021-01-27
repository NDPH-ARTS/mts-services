package uk.ac.ox.ndph.mts.sample_service.security;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    /**
     * Authorise request
     * @return true if authorised
     */
    @SuppressWarnings("squid:S2589") //allow using method that always return true, currently a stub algorithm
    public boolean authorise(String requiredPermission) {
        //currently
        String userId = "stubUserId";

        //get practitioner assignment role
        List<String> participantRoles = getParticipantRoles(userId);

        //get permissions for the the practitioner assignment roles
        List<String> permissions = participantRoles.stream().flatMap(role ->
                getRolePermissions(role).stream()).collect(Collectors.toList());

        //validate the required permission is present
        if (!permissions.contains(requiredPermission)) {
            return false;
        }

        //we will also update the practitioner roles to be only those who have the permissions to perform the action
        //currently just a stub to show we filtered them out
        List<String> updatedParticipantRoles = participantRoles.subList(0, 1);

        //Get entity site. Currently just a stub
        String entitySite = "stubSite";

        for (String role :  updatedParticipantRoles) {
            //Validate that one of the roles has the site that is allowed to work on the entity
            if (isSiteAuthorised(entitySite, role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get participant roles
     * @param userId - user id on the token
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    @SuppressWarnings("squid:S1172") //suppress unused parameter
    private List<String> getParticipantRoles(String userId) {
        return Arrays.asList("stubRole1", "stubRole2");
    }

    /**
     * Get user permissions
     * @param userRole - participant role
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    @SuppressWarnings("squid:S1172") //suppress unused parameter
    private List<String> getRolePermissions(String userRole) {
        return Collections.singletonList("stubPermission");
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
