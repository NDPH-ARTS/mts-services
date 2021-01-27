package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class PractitionerConfiguration {

    private final String name;
    private final String displayName;

    private final List<PractitionerAttributeConfiguration> attributes;

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
}
