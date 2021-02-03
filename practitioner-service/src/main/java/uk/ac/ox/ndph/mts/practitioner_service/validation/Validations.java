package uk.ac.ox.ndph.mts.practitioner_service.validation;

/**
 * String constants
 */
public enum Validations {
    STARTUP("Loaded practitioner validation with configuration: {}"),
    EXTERNAL_ENTITY_NOT_EXIST_ERROR("The value of %s doesn't exist"),
    ERROR("argument %s failed validation");

    private final String message;

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
