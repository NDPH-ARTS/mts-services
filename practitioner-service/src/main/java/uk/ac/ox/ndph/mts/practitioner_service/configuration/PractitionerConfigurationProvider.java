package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;

/**
 * Provide Practitioner Configuration 
 */
@Component
public class PractitionerConfigurationProvider {

    @Value("classpath:practitioner-configuration.json")
    Resource configurationFile;

    private PractitionerConfiguration configuration;

    /**
     * Get Practitioner Configuration
     * @return PractitionerConfiguration
     */
    public PractitionerConfiguration getConfiguration() {
        if (configuration == null) {
            try {
                String jsonString = new String(configurationFile.getInputStream().readAllBytes());
                configuration = new ObjectMapper().readValue(jsonString, PractitionerConfiguration.class);
            } catch (Exception e) {
                throw new InitialisationError(ConfigurationConsts.CONFIGURATION_ERROR_LOADING_LOG.getValue(), e);
            }
        }
        return configuration;
    }
}
