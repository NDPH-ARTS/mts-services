package uk.ac.ox.ndph.mts.roleserviceclient.model;

public enum Response {
    SERVICE_NAME_STATUS_AND_ID("service: %s responded with status: %s for id: %s"),
    SERVICE_NAME_STATUS_AND_ARGUMENTS("service: %s responded with status: %s for argument: %s");

    private final String message;

    Response(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
