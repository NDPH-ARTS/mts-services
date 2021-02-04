package uk.ac.ox.ndph.mts.init_service.model;

public class RoleAssignment {

    private String practitionerId;
    private String siteId;
    private String roleId;

    public RoleAssignment(String practitionerId, String siteId, String roleId) {
        this.practitionerId = practitionerId;
        this.siteId = siteId;
        this.roleId = roleId;
    }
    public String getPractitionerId() {
        return practitionerId;
    }
    public String getSiteId() {
        return siteId;
    }
    public String getRoleId() {
        return roleId;
    }

}
