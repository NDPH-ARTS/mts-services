package uk.ac.ox.ndph.mts.practitioner_service.service;

/**
 * String constants
 */
public enum ServiceConsts {
    PRACTITIONER_SERVICE_STARTUP_LOG("Loaded practitioner service required dependencies");

    private String value;
 
    ServiceConsts(String value) {
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
