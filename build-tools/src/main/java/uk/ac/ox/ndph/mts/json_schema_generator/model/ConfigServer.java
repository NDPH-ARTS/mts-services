package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigServer extends Service{
    @JsonProperty(value = "git_uri", required = true)
    private String gitUri;

    @JsonProperty(value = "search_paths", required = true)
    private String searchPaths;
}

