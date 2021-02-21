package uk.ac.ox.ndph.mts.site_service.service;

/**
 * String constants
 */
public enum Services {

    STARTUP("Loaded site service required dependencies"),
    NO_ROOT_SITE("No root site found"),
    SITE_NOT_FOUND("Site ID not found"),
    SITE_NAME_EXISTS("Site with given name exists"),
    ROOT_SITE_EXISTS("Root site already exists"),
    INVALID_ROOT_SITE("Invalid Site Type for Root"),
    INVALID_CHILD_SITE_TYPE("Invalid Child Site Type for Parent");

    private final String message;
 
    Services(String message) {
        this.message = message;
    }

    /**
    * Returns the string associated with the enum variant.
    * @return the string value of the enum variant
    */
    public String message() {
        return message;
    }
}
