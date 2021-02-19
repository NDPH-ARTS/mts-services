package uk.ac.ox.ndph.mts.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The service which performs the authorisation flow
 */
@Service
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final SecurityContextUtil securityContextUtil;

    @Autowired
    public AuthenticationService(final SecurityContextUtil securityContextUtil) {
        this.securityContextUtil = securityContextUtil;
    }

    /**
     * Authenticate request
     * @return true if userId in token matches the expected
     * identity
     */
    public boolean authenticate(String identity)  {

        return identity.equals(securityContextUtil.getUserId());
    }

}
