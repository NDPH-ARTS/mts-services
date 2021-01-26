package uk.ac.ox.ndph.mts.sample_service.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Global method security
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DenyMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    /**
     * Enforce custom method security
     * @return MethodSecurityMetadataSource - custom method security
     */
    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new AuthorisationMethodSecurityMetadataSource();
    }
}
