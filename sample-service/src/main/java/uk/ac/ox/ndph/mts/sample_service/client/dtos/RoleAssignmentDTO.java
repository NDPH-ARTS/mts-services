package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleAssignmentDTO {

    @JsonProperty(value = "siteId")
    private String siteId;

    @JsonProperty(value = "roleId")
    private String roleId;

    public RoleAssignmentDTO() { }

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
