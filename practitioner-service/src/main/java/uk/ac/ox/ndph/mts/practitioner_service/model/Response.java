package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.stereotype.Component;

@Component
public class Response {

    private final String id;

    public Response() {
        id = "";
    }

    public Response(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
