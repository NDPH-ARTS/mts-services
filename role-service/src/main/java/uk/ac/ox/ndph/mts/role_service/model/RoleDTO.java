package uk.ac.ox.ndph.mts.role_service.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


public class RoleDTO {

    @Getter
    @Setter
    @NotBlank
    String roleName;

}
