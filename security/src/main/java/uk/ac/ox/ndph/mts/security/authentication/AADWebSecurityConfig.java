package uk.ac.ox.ndph.mts.security.authentication;

// TODO: move to Spring Security libs: https://ndph-arts.atlassian.net/browse/ARTS-591

import com.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class AADWebSecurityConfig extends WebSecurityConfigurerAdapter {

    // TODO: move to Spring Security libs: https://ndph-arts.atlassian.net/browse/ARTS-591
    private AADAppRoleStatelessAuthenticationFilter aadAuthFilter;
    
    @Autowired
    public AADWebSecurityConfig(AADAppRoleStatelessAuthenticationFilter aadAuthFilter) {
        this.aadAuthFilter = aadAuthFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable csrf because we are using another token mechanism
        http.csrf().disable(); //NOSONAR

        http.cors(); // See https://www.baeldung.com/spring-cors

        // Do not create user sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // This requires all requests to have a valid token
        http.authorizeRequests() //NOSONAR
                .antMatchers("/actuator/health").permitAll() // allow health check without AuthN
                .antMatchers(SWAGGER_ALLOWLIST).permitAll() // allow swagger documentation without AuthN
                .anyRequest().authenticated();

        // This enables us to return appropriate http codes
        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.addFilterBefore(aadAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private static final String[] SWAGGER_ALLOWLIST = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v2/api-docs/**",
            "/swagger-ui/**"

    };
}
