package uk.ac.ox.ndph.mts.practitionerserviceclient.model;

import org.springframework.stereotype.Component;

@Component
public class ResponseDTO {

    private String id;

    public ResponseDTO() {
        id = "";
    }

    public ResponseDTO(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
