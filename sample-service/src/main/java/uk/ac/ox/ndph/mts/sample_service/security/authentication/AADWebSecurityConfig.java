package uk.ac.ox.ndph.mts.sample_service.security.authentication;

// TODO: move to Spring Security libs: https://ndph-arts.atlassian.net/browse/ARTS-591
import com.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The security configuration class to support AAD with JWT (no sessions)
 */
// The NOSONAR annotations in this class addresses a security hotspot which requires a known RBAC or ACL mechanism
// We have our custom authorisation mechanism which is not recognised by Sonar
@ConditionalOnProperty(prefix = "azure.activedirectory", value = "client-id")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AADWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    // TODO: move to Spring Security libs: https://ndph-arts.atlassian.net/browse/ARTS-591
    private AADAppRoleStatelessAuthenticationFilter aadAuthFilter;

    /**
     * Http security configuration
     * @param http - http security
     * @throws Exception - general exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable csrf because we are using another token mechanism
        http.csrf().disable(); //NOSONAR

        // Do not create user sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

        // This requires all requests to have a valid token
        http.authorizeRequests() //NOSONAR
                .anyRequest().authenticated();

        // This enables us to return appropriate http codes
        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.addFilterBefore(aadAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
