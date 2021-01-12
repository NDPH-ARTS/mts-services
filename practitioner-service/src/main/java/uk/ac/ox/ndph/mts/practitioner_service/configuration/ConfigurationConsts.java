package uk.ac.ox.ndph.mts.practitioner_service.configuration;

/**
 * String constants
 */
public enum ConfigurationConsts {
    CONFIGURATION_ERROR_LOADING_LOG("Error while loading configuration file");

    private String value;
 
    ConfigurationConsts(String value) {
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
