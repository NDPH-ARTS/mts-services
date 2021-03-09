package uk.ac.ox.ndph.mts.client.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteDTO {

    @JsonProperty(value = "siteId")
    private String siteId;

    @JsonProperty(value = "parentSiteId")
    private String parentSiteId;

    public SiteDTO() {
        //constructor used for deserialization
    }

    public SiteDTO(String siteId, String parentSiteId) {
        this.siteId = siteId;
        this.parentSiteId = parentSiteId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getParentSiteId() {
        return parentSiteId;
    }

    public void setParentSiteId(String parentSiteId) {
        this.parentSiteId = parentSiteId;
    }
}
