package uk.ac.ox.ndph.mts.role_service.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


public class RoleDTO {

    @Getter
    @Setter
    @NotBlank (message = "Role ID cannot be empty")
    @Size(max = 255, message = "Role ID is too long")
    String id;

    @Getter
    @Setter
    List<PermissionDTO> permissions = new ArrayList<>();

}
