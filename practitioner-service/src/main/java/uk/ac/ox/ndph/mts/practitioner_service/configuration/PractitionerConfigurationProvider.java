package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerAttributeConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;

import java.util.List;

/**
 * Provide Practitioner Configuration
 */
@Component
public class PractitionerConfigurationProvider {
    @Autowired
    PractitionerConfiguration practitionerConfiguration;


    /**
     * Get Practitioner Configuration
     * @return PractitionerConfiguration
     */
    public PractitionerConfiguration getConfiguration() {
        return practitionerConfiguration;

    }
}
