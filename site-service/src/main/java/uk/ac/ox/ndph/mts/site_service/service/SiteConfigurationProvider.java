package uk.ac.ox.ndph.mts.site_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.exception.ServerError;

/**
 * Provide Site Configuration 
 */
@Component
public class SiteConfigurationProvider {

    private static final String ERROR_LOADING_CONFIGURATION = "Error while loading configuration file";

    @Value("classpath:site-configuration.json")
    Resource configurationFile;

    private SiteConfiguration configuration;

    /**
     * Get Site Configuration
     * @return SiteConfiguration
     */
    public SiteConfiguration getConfiguration() {
        if (configuration == null) {
            try {
                String jsonString = new String(configurationFile.getInputStream().readAllBytes());
                configuration = new ObjectMapper().readValue(jsonString, SiteConfiguration.class);
            } catch (Exception e) {
                throw new ServerError(ERROR_LOADING_CONFIGURATION, e);
            }
        }
        return configuration;
    }
}
