package uk.ac.ox.ndph.mts.practitioner_service;

/**
 * String constants
 */
public enum Consts {
    
    // TODO: take static strings from resource file
    PRACTITIONER_APPLICATION_STARTUP_LOG("Staring practitioner service..."),
    VALIDATION_STARTUP_LOG("Laoded practitioner validation with configuration: {}"),
    VALIDATION_ERROR_MESSAGE("argument %s failed validation"),
    PRACTITIONER_SERVICE_STARTUP_LOG("Loaded practitioner service required dependencies"),
    FHIR_REPO_SAVE_PRACTITIONER_LOG("request to fhir: %s"),
    FHIR_REPO_SAVE_RESPONSE_LOG("response from fhir: %s"),
    FHIR_REPO_ERROR_UPDATE_LOG("error while updating fhir store"),
    FHIR_REPO_BAD_RESPONSE_SIZE_LOG("bad response size from FHIR: %d"),
    CONFIGURATION_ERROR_LOADING_LOG("Error while loading configuration file"),
    ATTRIBUTE_FROM_STRING_ERROR("cannot convert %s to Attribute enum");
    
    private String value;
 
    Consts(String value) {
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
