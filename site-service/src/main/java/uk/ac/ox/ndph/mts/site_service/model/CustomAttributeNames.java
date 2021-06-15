package uk.ac.ox.ndph.mts.site_service.model;

/**
 * site Attribute Names
 */
public enum CustomAttributeNames {
    ADDRESS("address");

    private final String attributeName;

    CustomAttributeNames(String attributeName) {
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
