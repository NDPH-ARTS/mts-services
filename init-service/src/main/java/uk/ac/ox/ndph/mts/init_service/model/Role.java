package uk.ac.ox.ndph.mts.init_service.model;

import java.util.List;

public class Role implements Entity {
    private String id;
    private List<Permission> permissions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return String.format("Role{id='%s', permissions='%s'}", id, permissions);
    }
}
