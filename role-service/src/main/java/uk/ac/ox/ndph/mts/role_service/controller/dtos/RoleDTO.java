package uk.ac.ox.ndph.mts.role_service.controller.dtos;



import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


public class RoleDTO {

    @NotBlank (message = "Role ID cannot be empty")
    @Size(max = 255, message = "Role ID is too long")
    private String id;
    private List<PermissionDTO> permissions;

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
