package uk.ac.ox.ndph.mts.site_service.validation;

/**
 * String constants
 */
public enum Validations {
    STARTUP("Laoded practitioner validation with configuration: {}"),
    ERROR("argument %s failed validation");

    private String message;
 
    Validations(String message) {
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
