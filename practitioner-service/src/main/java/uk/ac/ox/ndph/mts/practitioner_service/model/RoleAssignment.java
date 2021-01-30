package uk.ac.ox.ndph.mts.practitioner_service.model;

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

    public void setPractitionerId(String practitionerId) {
        this.practitionerId = practitionerId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
