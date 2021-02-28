package uk.ac.ox.ndph.mts.site_service.model;

/**
 * String constants
 */
public enum Models {
    STRING_PARSE_ERROR("cannot convert %s to Attribute enum");

    private final String error;
 
    Models(String error) {
        this.error = error;
    }

    /**
    * Returns the string associated with the enum variant.
    * @return the string value of the enum variant
    */
    public String error() {
        return error;
    }
}
