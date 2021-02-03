package uk.ac.ox.ndph.mts.practitioner_service.repository;

/**
 * String constants
 */
public enum FhirRepo {
    SAVE_REQUEST("request to fhir: {}"),
    SAVE_RESPONSE("response from fhir: {}"),
    BAD_RESPONSE_SIZE("bad response size from FHIR: %d"),
    PROBLEM_EXECUTING_TRANSACTION("Problem executing transaction with bundle at: %s"),
    FAILED_TO_SAVE_PRACTITIONER("Failed to save practitioner"),
    GET_PRACTITIONER_ROLES_BY_IDENTIFIER("get practitioner roles by identifier %s"),
    GET_PRACTITIONER_ROLES_BY_IDENTIFIER_RESPONSE("found %d practitioner roles");

    private final String message;
 
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
