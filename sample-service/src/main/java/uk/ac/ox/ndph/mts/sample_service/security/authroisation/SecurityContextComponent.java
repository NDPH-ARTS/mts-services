package uk.ac.ox.ndph.mts.sample_service.security.authroisation;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security context service to get data from the security context
 */
@Component
public class SecurityContextComponent {

    public UserPrincipal getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserPrincipal) authentication.getPrincipal());
    }
}
