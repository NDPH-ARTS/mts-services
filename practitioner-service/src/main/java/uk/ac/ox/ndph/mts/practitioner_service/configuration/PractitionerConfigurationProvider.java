package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;

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
