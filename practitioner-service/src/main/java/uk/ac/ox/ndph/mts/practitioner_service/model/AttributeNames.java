package uk.ac.ox.ndph.mts.practitioner_service.model;

/**
 * practitioner Attribute Names
 */
public enum AttributeNames {
    PREFIX("prefix"),
    GIVEN_NAME("givenName"),
    FAMILY_NAME("familyName");

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
