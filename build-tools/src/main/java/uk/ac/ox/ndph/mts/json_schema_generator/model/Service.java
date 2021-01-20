package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {
    @JsonProperty(value = "image_name", required = true)
    private String imageName;

    @JsonProperty(value = "image_tag", required = true)
    private String imageTag;
}
