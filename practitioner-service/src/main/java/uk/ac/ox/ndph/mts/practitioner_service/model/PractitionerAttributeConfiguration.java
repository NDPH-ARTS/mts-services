package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.stereotype.Component;

@Component
public class PractitionerAttributeConfiguration {

    private final String name;
    private final String displayName;
    private final String validationRegex;

    public PractitionerAttributeConfiguration() {
        this.name = "";
        this.displayName = "";
        this.validationRegex = "";
    }

    public PractitionerAttributeConfiguration(final String name, final String displayName, final String validationRegex) {
        this.name = name;
        this.displayName = displayName;
        this.validationRegex = validationRegex;
    }

    @Override
    public String toString() {
        // TODO(archiem) throwing exception to find out where this is used (part of Lombok-removal)
        throw new UnsupportedOperationException("Not implemented yet");
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
}
