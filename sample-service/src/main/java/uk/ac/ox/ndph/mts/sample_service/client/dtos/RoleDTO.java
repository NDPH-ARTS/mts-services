package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RoleDTO {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "permissions")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<PermissionDTO> permissions;

    public RoleDTO(String id, List<PermissionDTO> permissions) {
        this.id = id;
        this.permissions = permissions;
    }

    public RoleDTO() { }

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
