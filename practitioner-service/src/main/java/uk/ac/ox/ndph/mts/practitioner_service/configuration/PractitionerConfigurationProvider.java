package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;
import java.io.File;

/**
 * Provide Practitioner Configuration 
 */
@Component
public class PractitionerConfigurationProvider {

    @Value("${spring.cloud.config.uri}")
    private String configURI;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Value("${spring.cloud.config.label}")
    private String label;


    /**
     * Get Practitioner Configuration
     * @return PractitionerConfiguration
     */
    public PractitionerConfiguration getConfiguration() {

        PractitionerConfiguration practitionerConfiguration;

        try {
            practitionerConfiguration = WebClient.create().get()
                .uri(getRepoURL() + File.separator + "practitioner-configuration.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PractitionerConfiguration.class)
                .block();
        } catch (WebClientException wce) {
            throw new RestException(wce.getMessage());
        }

        return practitionerConfiguration;

    }

    private String getRepoURL() {
        return configURI + File.separator + applicationName + File.separator + profile + File.separator + label;
    }

}
