package uk.ac.ox.ndph.mts.siteserviceclient.model;

import org.springframework.stereotype.Component;

/**
 * A response from site service
 */
@Component
public class SiteResponseDTO {
    private String id;

    /**
     * Response default Constructor with no parameters
     *
     */
    public SiteResponseDTO() {
    }

    /**
     * Response Constructor with one parameter
     *
     * @param id the Response id
     */
    public SiteResponseDTO(String id) {
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
