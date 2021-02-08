package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Permission Data Transfer object
 */
public class PermissionDTO {

    @JsonProperty(value = "id")
    private String id;

    public PermissionDTO() {
        //constructor used for deserialization
    }

    /**
     * Get PermissionDTO id
     * @return string id
     */
    public String getId() {
        return id;
    }

    /**
     * Set PermissionDTO id
     * @param id permission dto id
     */
    public void setId(String id) {
        this.id = id;
    }
}
