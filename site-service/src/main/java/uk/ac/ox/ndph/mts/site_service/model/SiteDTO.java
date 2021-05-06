package uk.ac.ox.ndph.mts.site_service.model;

import uk.ac.ox.ndph.mts.siteserviceclient.model.Entity;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteAddressDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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
    private LocalDateTime lastUpdated;
    private String description;
    private String status;
    private String parentSiteName;

    public SiteDTO() {
        //constructor used for deserialization
    }

    public SiteDTO(String siteId, String parentSiteId) {
        this.siteId = siteId;
        this.parentSiteId = parentSiteId;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Site{name='%s', alias='%s', type='%s', address='%s'}", name, alias, siteType, address);
    }

    public String getParentSiteName() {
        return parentSiteName;
    }

    public void setParentSiteName(String parentSiteName) {
        this.parentSiteName = parentSiteName;
    }
}
