package uk.ac.ox.ndph.mts.site_service.model;

import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;

import java.util.function.Function;

/**
 * The known Site attributes. These are the static attributes, so configuration for them is required to be present.
 */
public enum CoreAttribute {

    NAME(CoreAttributeNames.NAME.nameof(), Site::getName),
    ALIAS(CoreAttributeNames.ALIAS.nameof(), Site::getAlias),
    PARENT_SITE_ID(CoreAttributeNames.PARENT_SITE_ID.nameof(), Site::getParentSiteId),
    SITE_TYPE(CoreAttributeNames.SITE_TYPE.nameof(), Site::getSiteType);

    private final String attributeName;
    private final Function<Site, Object> valueFunc;

    CoreAttribute(String attributeName, Function<Site, Object> value) {
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
    public Function<Site, Object> getValueFunc() {
        return valueFunc;
    }

    /**
     * Maps a string to an Attribute
     * @param input - The string to convert
     * @return converter - a model-entity to fhir-entity converter
     * @throws InitialisationError when string does not map to a known attribute.
     */
    public static CoreAttribute fromString(String input) throws InitialisationError {
        if (CoreAttributeNames.NAME.nameof().equals(input)) {
            return NAME;
        } else if (CoreAttributeNames.ALIAS.nameof().equals(input)) {
            return ALIAS;
        } else if (CoreAttributeNames.PARENT_SITE_ID.nameof().equals(input)) {
            return PARENT_SITE_ID;
        } else if (CoreAttributeNames.SITE_TYPE.nameof().equals(input)) {
            return SITE_TYPE;
        }
        throw new InitialisationError(String.format(Models.STRING_PARSE_ERROR.error(), input));
    }

}
