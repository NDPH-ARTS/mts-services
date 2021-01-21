package uk.ac.ox.ndph.mts.role_service.controller.dtos;


import javax.validation.constraints.NotBlank;


public class PermissionDTO {

    @NotBlank (message = "Permission ID cannot be empty")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
