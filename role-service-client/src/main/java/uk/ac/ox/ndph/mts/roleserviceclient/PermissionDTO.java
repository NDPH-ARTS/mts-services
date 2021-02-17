package uk.ac.ox.ndph.mts.roleserviceclient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PermissionDTO {

    @JsonProperty(value = "id")
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
