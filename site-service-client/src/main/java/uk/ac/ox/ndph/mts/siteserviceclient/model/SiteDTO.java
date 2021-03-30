package uk.ac.ox.ndph.mts.siteserviceclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SiteDTO implements Entity {

    private String name;

    private String alias;

    @JsonProperty(value = "siteId")
    private String siteId;

    @JsonProperty(value = "parentSiteId")
    private String parentSiteId;

    private String siteType;

    private SiteAddress address;

    public SiteDTO() {
        //constructor used for deserialization
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public SiteAddress getAddress() {
        return address;
    }

    public void setAddress(SiteAddress address) {
        this.address = address;
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

    @Override
    public String toString() {
        return String.format("Site{name='%s', alias='%s', type='%s', address='%s'}", name, alias, siteType, address);
    }

}
