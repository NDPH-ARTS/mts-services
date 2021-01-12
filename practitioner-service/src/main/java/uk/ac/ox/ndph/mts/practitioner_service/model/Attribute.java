package uk.ac.ox.ndph.mts.practitioner_service.model;

import java.util.function.Function;
import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;

/**
 * The known Practitioner attributes.
 * TODO: These should be generated dynamically from the configuration json.
 */
public enum Attribute {

    PREFIX(ModelConstants.ATTRIBUTE_NAME_PREFIX, Practitioner::getPrefix),
    GIVEN_NAME(ModelConstants.ATTRIBUTE_NAME_GIVEN_NAME, Practitioner::getGivenName),
    FAMILY_NAME(ModelConstants.ATTRIBUTE_NAME_FAMILY_NAME, Practitioner::getFamilyName);

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
        if (ModelConstants.ATTRIBUTE_NAME_PREFIX.equals(input)) {
            return PREFIX;
        } else if (ModelConstants.ATTRIBUTE_NAME_GIVEN_NAME.equals(input)) {
            return GIVEN_NAME;
        } else if (ModelConstants.ATTRIBUTE_NAME_FAMILY_NAME.equals(input)) {
            return FAMILY_NAME;
        }
        throw new InitialisationError(String.format(ModelConsts.ATTRIBUTE_FROM_STRING_ERROR.getValue(), input));
    }
}
