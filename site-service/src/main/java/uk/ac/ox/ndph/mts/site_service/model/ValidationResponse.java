package uk.ac.ox.ndph.mts.site_service.model;

/**
 * response from validation
 */
public final class ValidationResponse {

    private final boolean isValid;
    private final String errorMessage;

    private ValidationResponse(final boolean isValid, final String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public static ValidationResponse ok() {
        return new ValidationResponse(true, null);
    }

    public static ValidationResponse invalid(final String errorMessage) {
        return new ValidationResponse(false, errorMessage);
    }

    public boolean isValid() {
        return this.isValid;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String toString() {
        return "ValidationResponse(isValid=" + this.isValid() + ", errorMessage=" + this.getErrorMessage() + ")";
    }
    
}
