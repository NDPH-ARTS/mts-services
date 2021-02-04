package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpringCloud {
    @JsonProperty(value = "profile", required = true)
    private String profile;

    @JsonProperty(value = "label", required = true)
    private String label;

    @JsonProperty(value = "gateway_service", required = true)
    private Service gatewayService;

    @JsonProperty(value = "discovery_service", required = true)
    private Service discoveryService;

    @JsonProperty(value = "config_server", required = true)
    private ConfigServer configServer;
}
