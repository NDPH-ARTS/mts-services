package uk.ac.ox.ndph.mts.security.authorisation;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Global method security
 */
// The NOSONAR annotations in this class addresses a security hotspot which requires a known RBAC or ACL mechanism
// We have our custom authorisation mechanism which is not recognised by Sonar
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DenyMethodSecurityConfig extends GlobalMethodSecurityConfiguration { //NOSONAR

    /**
     * TODO: Uncomment when authorisation integration is done
     * Enforce custom method security
     * @return MethodSecurityMetadataSource - custom method security
    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new AuthorisationMethodSecurityMetadataSource();
    }
    */
}
