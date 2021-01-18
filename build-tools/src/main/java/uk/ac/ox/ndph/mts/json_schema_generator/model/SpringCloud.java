package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpringCloud {
    @JsonProperty(value = "gateway_service", required = true)
    private Service gatewayService;

    @JsonProperty(value = "discovery_service", required = true)
    private Service discoveryService;

    @JsonProperty(value = "config_service", required = true)
    private Service configService;
}
