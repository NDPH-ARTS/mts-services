package uk.ac.ox.ndph.mts.init_service.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class SiteDTO implements Entity {
    @NotBlank
    private String name;
    private String alias;
    @NotBlank
    private String siteType;
    @Valid
    private SiteAddressDTO address;
    private String siteId;
    private String parentSiteId;

    public SiteDTO() {
        //constructor used for deserialization
    }

    public SiteDTO(String siteId, String parentSiteId) {
        this.siteId = siteId;
        this.parentSiteId = parentSiteId;
    }

    public SiteDTO(String siteId, String name, String alias, String parentId, String siteType) {
        this.siteId = siteId;
        this.name = name;
        this.alias = alias;
        parentSiteId = parentId;
        this.siteType = siteType;
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

    public SiteAddressDTO getAddress() {
        return address;
    }

    public void setAddress(SiteAddressDTO address) {
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
