package uk.ac.ox.ndph.mts.init_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

public class RoleDTO implements Entity {


    private String id;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<PermissionDTO> permissions;

    public RoleDTO() {
        //constructor used for deserialization
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
