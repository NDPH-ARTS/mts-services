package uk.ac.ox.ctsu.arts.sitetypeservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/sitetype").permitAll()
            .anyRequest().authenticated()
            .and().oauth2ResourceServer(oauth2 -> oauth2.jwt());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger-ui/**", "/v2/api-docs/**", "/configuration/ui",
                                   "/swagger-resources/**", "/swagger-ui", "/configuration/**", "/swagger-ui.html",
                                   "/webjars/**", "/favicon.ico");
    }
}

