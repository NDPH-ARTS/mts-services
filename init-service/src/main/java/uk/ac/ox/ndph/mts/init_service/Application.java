package uk.ac.ox.ndph.mts.init_service;

/**
 * String constants
 */
public enum Application {

    // TODO: take static strings from resource file
    STARTUP("Starting init service...");

    private final String message;

    Application(String message) {
        this.message = message;
    }

    /**
     * Returns the string associated with the enum variant.
     *
     * @return the string value of the enum variant
     */
    public String message() {
        return message;
    }
}
