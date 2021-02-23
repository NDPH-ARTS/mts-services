package uk.ac.ox.ndph.mts.site_service.configuration;

/**
 * String constants
 */
public enum Configurations {
    ERROR("Error while loading configuration file");

    private final String message;
 
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
