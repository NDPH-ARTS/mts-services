package uk.ac.ox.ndph.mts.site_service.model;

import java.time.LocalDateTime;
import java.util.Map;

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
     * Site Constructor with name alias and parent
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
     * Site Constructor with siteId name alias and parent
     * @param name the Site name
     * @param alias the Site alias
     * @param parentSiteId the Site parentSiteId
     * @param siteType the Site siteType
     *
     */
    public Site(String name, String alias, String parentSiteId, String siteType) {
        this.name = name;
        this.alias = alias;
        this.parentSiteId = parentSiteId;
        this.siteType = siteType;
    }

    /**
     * Site Constructor with siteId name alias parent siteType
     * @param siteId site ID
     * @param name the Site name
     * @param alias the Site alias
     * @param parentSiteId the Site parentSiteId
     * @param siteType the Site siteType
     *
     */
    public Site(final String siteId, String name, String alias, String parentSiteId, String siteType) {
        this(name, alias, parentSiteId, siteType);
        this.siteId = siteId;
    }

    /**
     * Site Constructor with siteId name alias parent siteType
     * @param siteId site ID
     * @param name the Site name
     * @param alias the Site alias
     * @param parentSiteId the Site parentSiteId
     * @param siteType the Site siteType
     * @param description the site description
     *
     */
    public Site(final String siteId, String name, String alias, String parentSiteId,
                String siteType, String description, String status) {
        this(name, alias, parentSiteId, siteType);
        this.siteId = siteId;
        this.description = description;
        this.status = status;
    }

    /**
     * Site Constructor with siteId name alias parent siteType
     * @param siteId site ID
     * @param name the Site name
     * @param alias the Site alias
     * @param parentSiteId the Site parentSiteId
     * @param siteType the Site siteType
     * @param description
     * @param lastUpdated lastUpdated timestamp
     *
     */
    public Site(final String siteId, String name, String alias, String parentSiteId,
                String siteType, String description, LocalDateTime lastUpdated, String status) {
        this(name, alias, parentSiteId, siteType);
        this.siteId = siteId;
        this.description = description;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }
    private String name;
    private String alias;
    private String siteId;
    private String parentSiteId;
    private String siteType;
    private SiteAddress address;
    private LocalDateTime lastUpdated;
    private String description;
    private String status;
    private Map<String, String> extensions;


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
     * Returns the siteType associated with the Site.
     * @return siteType the Site siteType.
     *
     */
    public String getSiteType() {
        return siteType;
    }

    /**
     * Sets the siteType of the Site.
     * @param siteType the Site siteType
     *
     */
    public void setSiteType(String siteType) {
        this.siteType = siteType;
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

    public SiteAddress getAddress() {
        return address;
    }

    public void setAddress(SiteAddress siteAddress) {
        this.address = siteAddress;
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

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }
}
