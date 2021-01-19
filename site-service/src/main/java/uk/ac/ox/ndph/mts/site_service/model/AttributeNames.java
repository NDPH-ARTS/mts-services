package uk.ac.ox.ndph.mts.site_service.model;

/**
 * practitioner Attribute Names
 */
public enum AttributeNames {
    NAME("name"),
    ALIAS("alias");

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
