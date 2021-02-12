package uk.ac.ox.ndph.mts.practitioner_service.service;

/**
 * String constants
 */
public enum Services {
    STARTUP("Loaded practitioner service required dependencies"),
    PRACTITIONER_NOT_FOUND("PractitionerId not found");

    private final String message;
 
    Services(String message) {
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
