package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    private String CLIENT_ID;

    @Value("${swagger.authserver.url}")
    private String AUTH_SERVER;

    private List<AuthorizationScope> authorizationScopeList;

    WebConfig() {
        authorizationScopeList = new ArrayList<>();
    }

    @Bean
    public Docket api() {
        authorizationScopeList.add(new AuthorizationScope("api://mts-apps/admin", "admin scope"));


        return new Docket(DocumentationType.SWAGGER_2).select().apis(
            RequestHandlerSelectors.basePackage("uk.ac.ox.ndph.mts.trial_config_service.controller"))
                                                      .paths(PathSelectors.any()).build()
                                                      .securitySchemes(asList(securityScheme()))
                                                      .securityContexts(asList(securityContext()));
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().clientId(CLIENT_ID).clientSecret(CLIENT_SECRET)
                                           .scopeSeparator(" ").useBasicAuthenticationWithAccessCodeGrant(true).build();
    }

    private SecurityScheme securityScheme() {
        TokenRequestEndpoint token = new TokenRequestEndpointBuilder().url(AUTH_SERVER + "/authorize").build();
        TokenEndpoint authToken = new TokenEndpointBuilder().url(AUTH_SERVER + "/token").build();
        GrantType grantType = new AuthorizationCodeGrant(token, authToken);

        return new OAuthBuilder().grantTypes(asList(grantType)).scopes(authorizationScopeList).name("azure").build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(
            asList(new SecurityReference("azure", authorizationScopeList.toArray(new AuthorizationScope[]{})))).build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/trial-config/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
