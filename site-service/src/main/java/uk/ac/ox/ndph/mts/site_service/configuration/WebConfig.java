package uk.ac.ox.ndph.mts.site_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * This configuration enables the Swagger UI at /swagger-ui/
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Return the Swagger UI docket handler
     * @return Docket for Swagger UI
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(
                RequestHandlerSelectors.basePackage("uk.ac.ox.ndph.mts.site_service.controller"))
                .paths(PathSelectors.any()).build();
    }

}
