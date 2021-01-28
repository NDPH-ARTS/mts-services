package uk.ac.ox.ndph.mts.practitioner_service.configuration;

/**
 * String constants
 */
public enum Configurations {
    ERROR("Error loading configuration file");

    private String message;
 
    Configurations(String message) {
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
