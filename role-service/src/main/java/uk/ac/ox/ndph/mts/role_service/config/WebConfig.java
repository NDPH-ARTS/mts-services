package uk.ac.ox.ndph.mts.role_service.config;

import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.ArrayList;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.swagger.web.UiConfiguration.Constants.NO_SUBMIT_METHODS;


@Configuration

public class WebConfig implements WebMvcConfigurer {


    @Bean
    public Docket enableSwagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggerDisplayOptions())
                .select()
                .apis(basePackage("uk.ac.ox.ndph.mts.role_service.controller"))
                .paths(any())
                .build();
    }

    @Bean
    public UiConfiguration removeSwaggerTryItOutButton() {
        return UiConfigurationBuilder.builder()
                .supportedSubmitMethods(NO_SUBMIT_METHODS)
                .build();
    }

    private ApiInfo swaggerDisplayOptions() {
        return new ApiInfoBuilder()
                .title("Role Service API Documentation")
                .license("") // otherwise this defaults to Apache
                .licenseUrl("")
                .build();
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

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
