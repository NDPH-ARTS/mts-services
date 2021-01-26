package uk.ac.ox.ndph.mts.sample_service.security;

import com.microsoft.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;
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
@ConditionalOnProperty(prefix = "azure.activedirectory", value = "client-id")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AADWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AADAppRoleStatelessAuthenticationFilter aadAuthFilter;

    /**
     * Http security configuration
     * @param http - http security
     * @throws Exception - general exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable csrf because we are using another token mechanism
        http.csrf().disable();

        // Do not create user sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

        // This requires all requests to have a valid token
        http.authorizeRequests()
                .anyRequest().authenticated();

        // This enables us to return appropriate http codes
        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.addFilterBefore(aadAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
