package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.stereotype.Component;

/**
 * Site Attribute configuration Model
 */
@Component
public class SiteAttributeConfiguration {

    private String name;
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
     * @param name            attribute name can be null
     * @param displayName     atttribute display name can be null
     * @param validationRegex for regexc-based validation, can be null
     */
    public SiteAttributeConfiguration(final String name, final String displayName, final String validationRegex) {
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

    @Override
    public String toString() {
        return "SiteAttributeConfiguration{"
                + "name='" + name + '\''
                + ", displayName='" + displayName + '\''
                + ", validationRegex='" + validationRegex + '\''
                + '}';
    }


}
