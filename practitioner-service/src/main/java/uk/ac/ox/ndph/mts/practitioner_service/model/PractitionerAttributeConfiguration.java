package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "attributes")
@EnableConfigurationProperties
public class PractitionerAttributeConfiguration {

    private String name;
    private String displayName;
    private String validationRegex;

    public PractitionerAttributeConfiguration() {
        this.name = "";
        this.displayName = "";
        this.validationRegex = "";
    }

    public PractitionerAttributeConfiguration(final String name,
                                              final String displayName,
                                              final String validationRegex) {
        this.name = name;
        this.displayName = displayName;
        this.validationRegex = validationRegex;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }
}
