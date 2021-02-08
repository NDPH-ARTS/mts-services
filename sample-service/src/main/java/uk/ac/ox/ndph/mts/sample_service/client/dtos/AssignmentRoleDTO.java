package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Practitioner assignment role data transfer object
 */
public class AssignmentRoleDTO {

    @JsonProperty(value = "siteId")
    private String siteId;

    @JsonProperty(value = "roleId")
    private String roleId;

    public AssignmentRoleDTO() {
        //constructor used for deserialization
    }

    /**
     * Get assignment site id
     * @return string site id
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * Set assignment role site id
     * @param siteId assignment role site id
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * Get assignment role id
     * @return string assignment role id
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Set assignment role id
     * @param roleId string assignment role id
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
