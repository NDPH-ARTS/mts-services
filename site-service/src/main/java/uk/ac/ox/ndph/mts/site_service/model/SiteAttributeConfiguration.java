package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * Site Attribute configuration Model
 */
@Component
public class SiteAttributeConfiguration {

    @NotBlank
    private String name;
    private String type;
    @NotBlank
    private String displayName;
    private String validationRegex;

    /**
     * Constructor with no members initialized - use setters to set attributes
     */
    public SiteAttributeConfiguration() {
    }

    /**
     * Constructor with all members initialized, no validation checks here
     *
     * @param name attribute name can be null
     * @param type attribute type can be null
     * @param displayName attribute display name can be null
     * @param validationRegex for regex-based validation, can be null
     */
    public SiteAttributeConfiguration(final String name, final String type,
                                      final String displayName, final String validationRegex) {
        this.name = name;
        this.type = type;
        this.displayName = displayName;
        this.validationRegex = validationRegex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "SiteAttributeConfiguration{"
                + "name='" + name + '\''
                + ", type='" + type + '\''
                + ", displayName='" + displayName + '\''
                + ", validationRegex='" + validationRegex + '\''
                + '}';
    }


}
