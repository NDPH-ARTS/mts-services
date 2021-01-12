package uk.ac.ox.ndph.mts.practitioner_service.repository;

/**
 * String constants
 */
public enum RepositoryConsts {
    FHIR_REPO_SAVE_PRACTITIONER_LOG("request to fhir: %s"),
    FHIR_REPO_SAVE_RESPONSE_LOG("response from fhir: %s"),
    FHIR_REPO_ERROR_UPDATE_LOG("error while updating fhir store"),
    FHIR_REPO_BAD_RESPONSE_SIZE_LOG("bad response size from FHIR: %d");

    private String value;
 
    RepositoryConsts(String value) {
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
