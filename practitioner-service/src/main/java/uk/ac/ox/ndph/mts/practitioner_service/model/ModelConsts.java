package uk.ac.ox.ndph.mts.practitioner_service.model;

/**
 * String constants
 */
public enum ModelConsts {
    ATTRIBUTE_FROM_STRING_ERROR("cannot convert %s to Attribute enum");

    private String value;
 
    ModelConsts(String value) {
        this.value = value;
    }

    /**
    * Returns the string associated with the enum variant.
    * @return the string value of the enum variant
    */
    public String getValue() {
        return value;
    }
}
