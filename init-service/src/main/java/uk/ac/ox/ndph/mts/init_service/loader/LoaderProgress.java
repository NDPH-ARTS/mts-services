package uk.ac.ox.ndph.mts.init_service.loader;

public enum LoaderProgress {
    ENTRY_POINT("init service delaying for %d seconds."),
    GET_ROLES_FROM_CONFIG("getting roles from config."),
    CREATE_ROLES("creating roles."),
    GET_SITES_FROM_CONFIG("getting sites from config."),
    CREATE_SITES("creating sites."),
    GET_PERSONS_FROM_CONFIG("getting persons from config."),
    SELECT_FIRST_SITE_FROM_COLLECTION_OF_SIZE("selecting the first side id out of %d"),
    CREATE_PRACTITIONER("creating practitioner."),
    WAITING_FOR_ALL("waiting for all services to be registered"),
    N_SERVICES_REGISTERED("%d services are registered"),
    SERVICE_REGISTERED("service '%s' is registered"),
    ALL_REGISTERED("all services were registered. Continue"),
    //These are token messages, if you change this you need to change in github action as well!
    FINISHED_SUCCESSFULY("***SUCCESS***"),
    FAILURE("***FAILURE***");

    private final String message;

    LoaderProgress(String message) {
        this.message = message;
    }

    /**
     * Returns the message associated with the enum variant.
     *
     * @return the message value of the enum variant
     */
    public String message() {
        return message;
    }
}
