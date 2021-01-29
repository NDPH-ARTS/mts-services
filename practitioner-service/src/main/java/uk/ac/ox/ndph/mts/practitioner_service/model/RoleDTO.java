package uk.ac.ox.ndph.mts.practitioner_service.model;

import java.io.Serializable;

public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 987456232L;

    private String id;

    public RoleDTO() {
    }

    public RoleDTO(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String value) {
        this.id = value;
    }

    @Override
    public String toString() {
        return "RoleDTO(id='" + id + "\'}";
    }

}
