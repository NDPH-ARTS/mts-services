package uk.ac.ox.ndph.mts.practitioner_service.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 987456231L;

    @Getter @Setter
    String id;

   @Override
   public String toString() {
      return "RoleDTO{" +
              "id='" + id + '\'' +
              '}';
   }

}
