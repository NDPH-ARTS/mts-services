package uk.ac.ox.ndph.mts.fhir_client;

/**
 * String constants
 */
public enum Messages {
    EXECUTING_TRANSACTION_EXCEPTION("Problem executing transaction with bundle."),
    STRING_BLANK_EXCEPTION("Value of '%s' is null or empty");

    private final String message;
 
    Messages(String message) {
        this.message = message;
    }

    /**
    * Returns the message associated with the enum variant.
    * @return the message value of the enum variant
    */
    public String message() {
        return message;
    }

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
