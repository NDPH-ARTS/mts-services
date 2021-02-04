package uk.ac.ox.ndph.mts.site_service.model;

import java.util.Objects;

/**
 * Site Model
 */
public class Site {

    /**
     * Site No Args Constructor
     *
     */
    public Site() {
    }

    /**
     * Site Constructor with two parameters
     *
     * @param name the Site name
     * @param alias the Site alias
     */
    public Site(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    /**
     * Site Constructor with two parameters
     *
     * @param name the Site name
     * @param alias the Site alias
     * @param parentSiteId the Site parentSiteId
     *
     */
    public Site(String name, String alias, String parentSiteId) {
        this.name = name;
        this.alias = alias;
        this.parentSiteId = parentSiteId;
    }

    /**
     * Site factory with name alias parent and ID
     * make this a named factory to avoid mixing up site ID with parent site ID
     * @param siteId site ID (never null)
     * @param name the Site name
     * @param alias the Site alias
     * @param parentSiteId the Site parentSiteId
     * @return site the Site object
     */
    public static Site withIdNameAliasAndParent(final String siteId, String name, String alias, String parentSiteId) {
        final Site site = new Site(name, alias, parentSiteId);
        Objects.requireNonNull(siteId);
        site.setSiteId(siteId);
        return site;
    }

    private String name;
    private String alias;
    private String siteId;
    private String parentSiteId;

    /**
     * Returns the name associated with the Site.
     * @return name the Site name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Site.
     * @param name the Site name
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the alias associated with the Site.
     * @return alias the Site alias.
     *
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias of the Site.
     * @param alias the Site alias
     *
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Returns the parentSiteId associated with the Site.
     * @return parentSiteId the Site parentSiteId.
     *
     */
    public String getParentSiteId() {
        return parentSiteId;
    }

    /**
     * Sets the parentSiteId of the Site.
     * @param parentSiteId the Site parentSiteId
     *
     */
    public void setParentSiteId(String parentSiteId) {
        this.parentSiteId = parentSiteId;
    }

    /**
     * Returns the siteId associated with the Site.
     * @return siteId the Site siteId.
     *
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * Sets the siteId of the Site.
     * @param siteId the Site siteId
     *
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
