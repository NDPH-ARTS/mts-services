package uk.ac.ox.ndph.mts.role_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(
                RequestHandlerSelectors.basePackage("uk.ac.ox.ndph.mts.role_service.controller"))
                .paths(PathSelectors.any()).build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/role-service/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

}
