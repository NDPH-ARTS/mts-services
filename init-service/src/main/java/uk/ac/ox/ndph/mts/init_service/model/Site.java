package uk.ac.ox.ndph.mts.init_service.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class Site implements Entity {
    @NotBlank
    private String name;
    private String alias;
    @NotBlank
    private String siteType;
    @Valid
    private SiteAddress address;

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

    @Override
    public String toString() {
        return String.format("Site{name='%s', alias='%s', type='%s', address='%s'}", name, alias, siteType, address);
    }

}
