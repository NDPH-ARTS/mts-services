package uk.ac.ox.ndph.mts.init_service.model;

public class PermissionDTO implements Entity {

    private String id;

    public PermissionDTO() {
        //constructor used for deserialization
    }

    public PermissionDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
