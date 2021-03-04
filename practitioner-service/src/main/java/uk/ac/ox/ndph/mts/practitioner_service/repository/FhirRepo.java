package uk.ac.ox.ndph.mts.practitioner_service.repository;


public enum FhirRepo {
    SAVE_REQUEST("request to fhir: {}"),
    SAVE_RESPONSE("response from fhir: {}"),
    BAD_RESPONSE_SIZE("bad response size from FHIR: %d"),
    PROBLEM_EXECUTING_TRANSACTION("Problem executing transaction with bundle at: %s"),
    SEARCH_ERROR("Failed to search repository"),
    FAILED_TO_SAVE_PRACTITIONER("Failed to save practitioner"),
    GET_PRACTITIONERS_BY_USER_IDENTITY("get practitioners by user identity %s"),
    GET_PRACTITIONER_ROLES_BY_USER_IDENTITY("get practitioner roles by user identity %s"),
    GET_PRACTITIONER_ROLES_BY_USER_IDENTITY_RESPONSE("found %d practitioner roles");

    private final String message;

    FhirRepo(String message) {
        this.message = message;
    }


    public String message() {
        return message;
    }
}
