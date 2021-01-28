package uk.ac.ox.ndph.mts.practitioner_service.validation;

/**
 * String constants
 */
public enum Validations {
    STARTUP("Loaded practitioner validation with configuration from config server: {}"),
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
