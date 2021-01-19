package uk.ac.ox.ndph.mts.site_service.repository;

/**
 * String constants
 */
public enum FhirRepo {
    SAVE_ORGANIZATION("request to fhir: %s"),
    SAVE_RESEARCHSTUDY("request to fhir: %s"),
    SAVE_RESPONSE("response from fhir: %s"),
    UPDATE_ERROR("error while updating fhir store"),
    BAD_RESPONSE_SIZE("bad response size from FHIR: %d");

    private String message;
 
    FhirRepo(String message) {
        this.message = message;
    }

    /**
    * Returns the message associated with the enum variant.
    * @return the message value of the enum variant
    */
    public String message() {
        return message;
    }
}
