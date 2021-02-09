package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Practitioner role assignment data transfer object
 */
public class RoleAssignmentDTO {

    @JsonProperty(value = "siteId")
    private String siteId;

    @JsonProperty(value = "roleId")
    private String roleId;

    public RoleAssignmentDTO() {
        //constructor used for deserialization
    }

    /**
     * Get role assignment site id
     * @return string role assignment site id
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * Set role assignment site id
     * @param siteId role assignment site id
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * Get role assignment id
     * @return string role assignment id
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Set role assignment id
     * @param roleId string role assignment id
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
