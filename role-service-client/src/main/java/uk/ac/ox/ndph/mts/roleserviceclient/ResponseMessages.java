package uk.ac.ox.ndph.mts.roleserviceclient;

import java.util.function.Supplier;

public enum ResponseMessages implements Supplier<String> {
    SERVICE_NAME_STATUS_AND_PATH("service: %s responded with status: %s for path: %s"),
    ID_NOT_NULL("role ID must not be null"),
    LIST_NOT_NULL("list must not be null"),
    ROLE_NOT_NULL("list must not be null");

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
