package uk.ac.ox.ndph.mts.practitioner_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class PractitionerAttributeConfiguration {

    @JsonProperty(required = true)
    @NotBlank
    private String name;
    @JsonProperty(required = true)
    @NotBlank
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }
}
