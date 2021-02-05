package uk.ac.ox.ndph.mts.site_service.service;

/**
 * String constants
 */
public enum Services {
    STARTUP("Loaded site service required dependencies"),
    NO_ROOT_SITE("No root site found");

    private String message;
 
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
