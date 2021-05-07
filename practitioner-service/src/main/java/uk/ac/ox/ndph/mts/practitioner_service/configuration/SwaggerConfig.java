package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.swagger.web.UiConfiguration.Constants.NO_SUBMIT_METHODS;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {


    @Bean
    public Docket enableSwagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggerDisplayOptions())
                .select()
                .apis(basePackage("uk.ac.ox.ndph.mts.practitioner_service.controller"))
                .paths(any())
                .build();
    }

    @Bean
    public UiConfiguration removeSwaggerTryItOutButton() {
        return UiConfigurationBuilder.builder()
                .supportedSubmitMethods(NO_SUBMIT_METHODS)
                .build();
    }

    @Value("${mts.docs.title:MTS Practitioner Service API Docs}")
    private String title;

    private ApiInfo swaggerDisplayOptions() {
        return new ApiInfoBuilder()
                .title(title)
                .license("")
                .licenseUrl("")
                .build();
    }
}
