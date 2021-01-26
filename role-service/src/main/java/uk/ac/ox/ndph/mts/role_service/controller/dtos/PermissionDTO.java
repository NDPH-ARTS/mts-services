package uk.ac.ox.ndph.mts.role_service.controller.dtos;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class PermissionDTO {

    @NotBlank (message = "Permission ID cannot be empty")
    @Size(max = 255, message = "Permission ID is too long")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
