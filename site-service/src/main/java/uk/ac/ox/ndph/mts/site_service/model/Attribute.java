package uk.ac.ox.ndph.mts.site_service.model;

import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;

import java.util.function.Function;

/**
 * The known Site attributes. These are the static attributes, so configuration for them is required to be present.
 */
public enum Attribute {

    NAME(AttributeNames.NAME.nameof(), Site::getName),
    ALIAS(AttributeNames.ALIAS.nameof(), Site::getAlias),
    PARENT_SITE_ID(AttributeNames.PARENT_SITE_ID.nameof(), Site::getParentSiteId),
    SITE_TYPE(AttributeNames.SITE_TYPE.nameof(), Site::getSiteType);

    private final String attributeName;
    private final Function<Site, String> valueFunc;

    Attribute(String attributeName, Function<Site, String> value) {
        this.attributeName = attributeName;
        this.valueFunc = value;
    }
    
    /**
     * gets the attribute name
     * @return the name of attribute
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * gets the getter function for this attribute
     * @return a function that returns value from site
     */
    public Function<Site, String> getValueFunc() {
        return valueFunc;
    }

    /**
     * Maps a string to an Attribute
     * @param input - The string to convert
     * @return converter - a model-entity to fhir-entity converter
     * @throws InitialisationError when string does not map to a known attribute.
     */
    public static Attribute fromString(String input) throws InitialisationError {
        if (AttributeNames.NAME.nameof().equals(input)) {
            return NAME;
        } else if (AttributeNames.ALIAS.nameof().equals(input)) {
            return ALIAS;
        } else if (AttributeNames.PARENT_SITE_ID.nameof().equals(input)) {
            return PARENT_SITE_ID;
        } else if (AttributeNames.SITE_TYPE.nameof().equals(input)) {
            return SITE_TYPE;
        }
        throw new InitialisationError(String.format(Models.STRING_PARSE_ERROR.error(), input));
    }

}
