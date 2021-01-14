package uk.ac.ox.ndph.mts.practitioner_service;

/**
 * String constants
 */
public enum Controllers {
    
    // TODO: take static strings from resource file
    STARTUP("Staring practitioner service...");
    
    private String message;
 
    Controllers(String message) {
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
