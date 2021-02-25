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

    /**
     * Get user id from security context
     * @return string user id
     */
    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = ((UserPrincipal) authentication.getPrincipal());
        return userPrincipal.getClaim("oid").toString();
    }
}
