package uk.ac.ox.ndph.mts.practitioner_service.validation;

/**
 * String constants
 */
public enum ValidationConsts {
    VALIDATION_STARTUP_LOG("Laoded practitioner validation with configuration: {}"),
    VALIDATION_ERROR_MESSAGE("argument %s failed validation");

    private String value;
 
    ValidationConsts(String value) {
        this.value = value;
    }

    /**
    * Returns the string associated with the enum variant.
    * @return the string value of the enum variant
    */
    public String getValue() {
        return value;
    }
}
