package uk.ac.ox.ndph.mts.site_service.model;

/**
 * site Attribute Names
 */
public enum CoreAttributeNames {
    NAME("name"),
    ALIAS("alias"),
    PARENT_SITE_ID("parentSiteId"),
    SITE_TYPE("siteType"),
    DESCRIPTION("description"),
    STATUS("Status");

    private final String attributeName;

    CoreAttributeNames(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * gets the attribute name
     * @return the name of attribute
     */
    public String nameof() {
        return attributeName;
    }
}
