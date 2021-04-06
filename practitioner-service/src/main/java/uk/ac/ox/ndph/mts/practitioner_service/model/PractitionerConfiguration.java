package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Configuration
@Validated
@ConfigurationProperties(prefix = "mts.practitioner")
public class PractitionerConfiguration {

    @NotBlank
    private String name;
    @NotBlank
    private String displayName;
    @NotEmpty
    private List<@Valid PractitionerAttributeConfiguration> attributes;

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

    public void setAttributes(List<PractitionerAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
