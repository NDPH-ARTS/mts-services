package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Role data transfer object
 */
public class RoleDTO {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "permissions")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<PermissionDTO> permissions;

    public RoleDTO() {
        //constructor used for deserialization
    }

    /**
     * Get RoleDTO id
     * @return string id
     */
    public String getId() {
        return id;
    }

    /**
     * Set RoleDTO id
     * @param id RoleDTO id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get RoleDTO permissions
     * @return list of permissions
     */
    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    /**
     * Set RoleDTO permissions
     * @param permissions list of permissions
     */
    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
