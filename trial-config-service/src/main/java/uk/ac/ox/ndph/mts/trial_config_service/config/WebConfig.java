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
public class WebConfig implements WebMvcConfigurer, WebConfigService {

    private static final String API_MTS_APPS_ADMIN = "api://mts-apps/admin";
    private static final String ADMIN_SCOPE = "admin scope";

    // TODO ARTS-264 put these values into Azure Key Vault
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String clientSecret;

    // TODO ARTS-264 put these values into Azure Key Vault
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    private String clientId;

    // TODO ARTS-264 put these values into Azure Key Vault
    @Value("${swagger.authserver.url}")
    private String authServer;

    private List<AuthorizationScope> authorizationScopeList;

    WebConfig() {
        authorizationScopeList = new ArrayList<>();
    }

    @Bean
    public Docket api() {
        authorizationScopeList.add(new AuthorizationScope(API_MTS_APPS_ADMIN, ADMIN_SCOPE));


        return new Docket(DocumentationType.SWAGGER_2).select().apis(
            RequestHandlerSelectors.basePackage("uk.ac.ox.ndph.mts.trial_config_service.controller"))
                                                      .paths(PathSelectors.any()).build()
                                                      .securitySchemes(asList(securityScheme()))
                                                      .securityContexts(asList(securityContext()));
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().clientId(clientId).clientSecret(clientSecret)
                                           .scopeSeparator(" ").useBasicAuthenticationWithAccessCodeGrant(true).build();
    }

    private SecurityScheme securityScheme() {
        TokenRequestEndpoint token = new TokenRequestEndpointBuilder().url(authServer + "/authorize").build();
        TokenEndpoint authToken = new TokenEndpointBuilder().url(authServer + "/token").build();
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

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getAuthServer() {
        return authServer;
    }
}
