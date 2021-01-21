package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.stereotype.Component;

/**
 * A response from site service
 */
@Component
public class Response {
    private String id;

    /**
     * Response default Constructor with no parameters
     *
     */
    public Response() {
    }

    /**
     * Response Constructor with one parameter
     *
     * @param id the Response id
     */
    public Response(String id) {
        this.id = id;
    }

    /**
     * Returns the id associated with the Response.
     * @return id the Response id
     */
    public String getId() {
        return id;
    }
}
