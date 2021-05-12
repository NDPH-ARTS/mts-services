package uk.ac.ox.ndph.mts.init_service.converter;

public enum Status {
    ACTIVE("ACTIVE", true),
    INACTIVE("INACTIVE", false);

    private final String status;
    private final boolean booleanValue;

    Status(String status, boolean booleanValue) {
        this.status = status;
        this.booleanValue = booleanValue;
    }

    public String getValue() {
        return status;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}
