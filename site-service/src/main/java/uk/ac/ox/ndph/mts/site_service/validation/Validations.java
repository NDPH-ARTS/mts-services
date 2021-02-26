package uk.ac.ox.ndph.mts.site_service.validation;

/**
 * String constants
 */
public enum Validations {
    STARTUP("Loaded site validation with configuration: {}"),
    ERROR("argument %s failed validation"),
    MISSING_ATTRIBUTE("configuration missing at least one required attribute '%s'");

    private final String message;
 
    Validations(final String message) {
        this.message = message;
    }

    /**
    * Returns the string associated with the enum variant.
    * @return the string value of the enum variant
    */
    public String message() {
        return message;
    }
}
