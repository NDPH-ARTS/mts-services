package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebConfigTest {

    WebConfig webConfig = new WebConfig();

    @Test
    void corsConfigurationTest() {

        assertEquals(expectedCorsConfiguration().getAllowedMethods(), webConfig.getConfiguration().getAllowedMethods());
        assertEquals(expectedCorsConfiguration().getAllowedHeaders(), webConfig.getConfiguration().getAllowedHeaders());
    }

    private CorsConfiguration expectedCorsConfiguration(){

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        return config;
    }
}
