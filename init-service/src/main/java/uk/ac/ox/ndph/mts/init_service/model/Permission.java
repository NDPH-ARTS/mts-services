package uk.ac.ox.ndph.mts.init_service.model;

public class Permission {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Permission{id='%s'}", id);
    }
}
