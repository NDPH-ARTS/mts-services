package uk.ac.ox.ndph.mts.sample_service.security;

import org.springframework.stereotype.Service;

/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthorisationService {

    /**
     * Authorise request
     * @return true if authorised
     */
    public boolean authorise() {
        return true;
    }

    /**
     * Get participant roles
     * @param userId - user id on the token
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    private boolean getParticipantRole(String userId) {
        return true;
    }

    /**
     * Get user permissions
     * @param userRole - participant role
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    private boolean getPermission(String userRole) {
        return true;
    }

    /**
     * Validate user site trees allowed the entity site
     * @param userRole - participant role which includes the site property
     * @param entitySite - the requested entity site
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    private boolean isSiteAuthorised(String entitySite, String userRole) {
        return true;
    }
}
