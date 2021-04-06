package uk.ac.ox.ndph.mts.practitionerserviceclient;

import java.util.function.Supplier;

public enum ResponseMessages implements Supplier<String> {
    ID_NOT_NULL("site ID must not be null"),
    PRACTITIONER_NOT_NULL("list must not be null");

    private final String message;

    ResponseMessages(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    @Override
    public String get() {
        return message();
    }

    public String format(Object... args)  {
        return String.format(message(), args);
    }

}
