package uk.ac.ox.ndph.mts.practitioner_service.service;

import java.nio.file.Files;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ServerError;

/**
 * Provide Practitioner Configuration 
 */
@Component
public class PractitionerConfigurationProvider {

    private static final String ERROR_LOADING_CONFIGURATION = "Error while loading configuration file";

    @Value("classpath:practitioner-configuration.json")
    Resource configurationFile;

    private PractitionerConfiguration configuration;

    /**
     * Get Pracititioner Configuration
     * @return PractitionerConfiguration
     */
    public PractitionerConfiguration getConfiguration() {
        if (configuration == null) {
            try {
                String jsonString = new String(Files.readAllBytes(configurationFile.getFile().toPath()));
                configuration = new ObjectMapper().readValue(jsonString, PractitionerConfiguration.class);
            } catch (Exception e) {
                throw new ServerError(ERROR_LOADING_CONFIGURATION, e);
            }
        }
        return configuration;
    }
}
