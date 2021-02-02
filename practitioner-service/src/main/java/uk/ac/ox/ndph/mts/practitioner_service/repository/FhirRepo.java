package uk.ac.ox.ndph.mts.practitioner_service.repository;

/**
 * String constants
 */
public enum FhirRepo {
    SAVE_REQUEST("request to fhir: {}"),
    SAVE_RESPONSE("response from fhir: {}"),
    UPDATE_ERROR("error while updating fhir store"),
    BAD_RESPONSE_SIZE("bad response size from FHIR: %d"),
    GET_PRACTITIONER_ROLES_BY_IDENTIFIER("get practitioner roles by identifier %s"),
    GET_PRACTITIONER_ROLES_BY_IDENTIFIER_RESPONSE("found %d practitioner roles");

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
