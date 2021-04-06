package uk.ac.ox.ndph.mts.practitionerserviceclient.model;

public class RoleAssignmentDTO implements Entity {

    private String practitionerId;
    private String siteId;
    private String roleId;

    public RoleAssignmentDTO() {
    }

    public RoleAssignmentDTO(String practitionerId, String siteId, String roleId) {
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

    public void setPractitionerId(String practitionerId) {
        this.practitionerId = practitionerId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


}
