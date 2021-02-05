package uk.ac.ox.ndph.mts.site_service.repository;

/**
 * String constants
 */
public enum FhirRepo {
    REQUEST_PAYLOAD("request to fhir: %s"),
    RESPONSE_PAYLOAD("response from fhir: %s"),
    UPDATE_ERROR("error while updating fhir store"),
    SEARCH_ERROR("error while searching fhir store"),
    PROBLEM_EXECUTING_TRANSACTION("Problem executing transaction with bundle at: %s"),
    BAD_RESPONSE_SIZE("bad response size from FHIR: %d"),
    SITE_EXISTS("Site Already Exists"),
    SITE_DOESNT_MATCH_PARENT("Site Exists But Doesn't Match Parent");;

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
