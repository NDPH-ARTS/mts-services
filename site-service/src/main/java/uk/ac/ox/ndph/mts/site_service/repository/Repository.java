package uk.ac.ox.ndph.mts.site_service.repository;

/**
 * String constants
 */
public enum Repository {
    REQUEST_PAYLOAD("request to FHIR: %s"),
    RESPONSE_PAYLOAD("response from FHIR: %s"),
    UPDATE_ERROR("error while updating FHIR store"),
    BAD_RESPONSE_SIZE("bad response size from FHIR: %d"),
    SEARCH_ERROR("error while searching for resource: %s"),
    TRANSACTION_ERROR("Problem executing transaction with bundle at: %s");

    private final String message;

    Repository(String message) {
        this.message = message;
    }

    /**
     * Returns the message associated with the enum variant.
     *
     * @return the message value of the enum variant
     */
    public String message() {
        return message;
    }
}
