package uk.ac.ox.ndph.mts.site_service.service;

/**
 * String constants
 */
public enum Services {

    STARTUP("Loaded site service required dependencies"),
    NO_ROOT_SITE("No root site found"),
    SITE_NOT_FOUND("Site ID not found"),
    PARENT_NOT_FOUND("Parent site ID not found"),
    SITE_EXISTS("Site with given name exists"),
    ONE_ROOT_SITE("Root site already exists"),
    INVALID_ROOT_SITE("Invalid Site Type for Root"),
    INVALID_CHILD_SITE_TYPE("Invalid Child Site Type for Parent");

    private String message;
 
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
