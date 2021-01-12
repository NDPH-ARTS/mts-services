package uk.ac.ox.ndph.mts.practitioner_service;

/**
 * String constants
 */
public enum ControllerConsts {
    
    // TODO: take static strings from resource file
    PRACTITIONER_APPLICATION_STARTUP_LOG("Staring practitioner service...");
    
    private String value;
 
    ControllerConsts(String value) {
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
