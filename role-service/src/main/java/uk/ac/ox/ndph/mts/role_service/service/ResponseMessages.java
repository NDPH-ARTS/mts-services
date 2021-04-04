package uk.ac.ox.ndph.mts.role_service.service;

public enum ResponseMessages {

    ROLE_NOT_FOUND("Role %s not found"),
    PERMISSION_NOT_FOUND("Permission %s not found"),
    DUPLICATE_ROLE_ID("Duplicate role ID %s");

    private String message;

    ResponseMessages(String message) {
        this.message = message;
    }
    public String message() {
        return message;
    }
}
