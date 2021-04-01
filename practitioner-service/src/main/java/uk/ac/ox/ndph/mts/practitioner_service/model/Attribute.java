package uk.ac.ox.ndph.mts.practitioner_service.model;

import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;

import java.util.function.Function;

/**
 * The known Practitioner attributes.
 * TODO: These should be generated dynamically from the configuration json.
 */
public enum Attribute {

    PREFIX(AttributeNames.PREFIX.nameof(), Practitioner::getPrefix),
    GIVEN_NAME(AttributeNames.GIVEN_NAME.nameof(), Practitioner::getGivenName),
    FAMILY_NAME(AttributeNames.FAMILY_NAME.nameof(), Practitioner::getFamilyName);

    private String attributeName;
    private Function<Practitioner, String> getValue;

    Attribute(String attributeName, Function<Practitioner, String> getValue) {
        this.attributeName = attributeName;
        this.getValue = getValue;
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
     * @return a function that returns value from practitioner
     */
    public Function<Practitioner, String> getGetValue() {
        return getValue;
    }

    /**
     * Maps a string to an Attribute
     * @param input - The string to convert
     * @return converter - a model-entity to fhir-entity converter
     * @throws InitialisationError when string does not map to a known attribtue.
     */
    public static Attribute fromString(String input) throws InitialisationError {
        if (AttributeNames.PREFIX.nameof().equals(input)) {
            return PREFIX;
        } else if (AttributeNames.GIVEN_NAME.nameof().equals(input)) {
            return GIVEN_NAME;
        } else if (AttributeNames.FAMILY_NAME.nameof().equals(input)) {
            return FAMILY_NAME;
        }
        throw new InitialisationError(String.format(Models.STRING_PARSE_ERROR.error(), input));
    }
}
