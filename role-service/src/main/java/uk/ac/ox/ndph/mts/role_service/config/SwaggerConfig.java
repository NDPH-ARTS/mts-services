package uk.ac.ox.ndph.mts.role_service.config;

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
                    .title("MTS Role Service API Documentation")
                    .license("") // Override otherwise this defaults to Apache in the display.  Our licence is TBC.
                    .licenseUrl("")
                    .build();
        }
}
