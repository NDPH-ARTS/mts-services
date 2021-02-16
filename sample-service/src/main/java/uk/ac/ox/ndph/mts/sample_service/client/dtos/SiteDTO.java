package uk.ac.ox.ndph.mts.sample_service.client.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SiteDTO {

    @JsonProperty(value = "siteId")
    private String siteId;

    @JsonProperty(value = "parentSiteId")
    private String parentSiteId;

    public SiteDTO() {
        //constructor used for deserialization
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
