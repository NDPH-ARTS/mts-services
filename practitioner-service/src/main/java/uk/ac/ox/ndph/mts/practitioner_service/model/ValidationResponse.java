package uk.ac.ox.ndph.mts.practitioner_service.model;

public class ValidationResponse {

    private final boolean isValid;
    private final String errorMessage;

    public ValidationResponse(final boolean isValid, final String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
