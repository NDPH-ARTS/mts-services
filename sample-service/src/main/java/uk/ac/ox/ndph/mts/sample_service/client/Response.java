package uk.ac.ox.ndph.mts.sample_service.client;

public enum Response {
    CLIENT_ERROR_RESPONSE("service: %s responded with status: %s for id: %s");

    private final String message;

    Response(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
