package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "config")
@EnableConfigurationProperties
public class PractitionerConfiguration {

    private String name;
    private String displayName;
    private List<PractitionerAttributeConfiguration> attributes;

    public PractitionerConfiguration() {
        this.name = "";
        this.displayName = "";
        attributes = Collections.emptyList();
    }

    public PractitionerConfiguration(final String name,
                                     final String displayName,
                                     final List<PractitionerAttributeConfiguration> attributes) {
        this.name = name;
        this.displayName = displayName;
        this.attributes = attributes;
    }

    public Collection<PractitionerAttributeConfiguration> getAttributes() {
        return Collections.unmodifiableCollection(attributes);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAttributes(List<PractitionerAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }
}
