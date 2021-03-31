package uk.ac.ox.ndph.mts.roleserviceclient.model;

public class PermissionDTO implements Entity {

    private String id;

    public PermissionDTO() {
        //constructor used for deserialization
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
