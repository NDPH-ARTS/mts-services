package uk.ac.ox.ndph.mts.security.authentication;

// TODO: move to Spring Security libs: https://ndph-arts.atlassian.net/browse/ARTS-591
import com.azure.spring.autoconfigure.aad.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security context util to get data from the security context
 */
@Component
public class SecurityContextUtil {

    private static final String IDENTITY_PROVIDER_ROLE = "internal.service";

    /**
     * Get user id from security context
     * @return string user id
     */
    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = ((UserPrincipal) authentication.getPrincipal());
        return userPrincipal.getClaim("oid").toString();
    }

    /**
     * Validate if the token contains a role of internal services identity provider
     * @return true if is in identity provider role
     */
    public boolean isInIdentityProviderRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = ((UserPrincipal) authentication.getPrincipal());
        return userPrincipal.getRoles().contains(IDENTITY_PROVIDER_ROLE);
    }

    /**
     * Get user id from security context
     * @return string user id
     */
    public String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = ((UserPrincipal) authentication.getPrincipal());
        return userPrincipal.getAadIssuedBearerToken();
    }
}
