package uk.ac.ox.ndph.mts.site_service.model;

/**
 * site Attribute Names
 */
public enum AttributeNames {
    NAME("name"),
    ALIAS("alias"),
    PARENT_SITE_ID("parentSiteId");

    private String attributeName;

    AttributeNames(String attributeName) {
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
