package uk.ac.ox.ndph.mts.practitioner_service.model;

public class ValidationResponse {

    private final boolean isValid;
    private final String errorMessage;

    public ValidationResponse(final boolean isValid, final String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        // TODO(archiem) throwing exception to find out where this is used (part of Lombok-removal)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
