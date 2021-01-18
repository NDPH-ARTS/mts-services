package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrialConfigFile {
    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private String id;

    @JsonProperty(value = "tenant_id", required = true)
    private String tenantId;

    @JsonProperty(value = "ui_client_id", required = true)
    private String uiClientId;

    @JsonProperty(required = true, defaultValue = "1")
    private int version;

    @JsonProperty(required = true)
    private String rootUser;

    @JsonProperty(value = "site_service", required = true)
    private Service siteService;

    @JsonProperty(value = "practitioner_service", required = true)
    private Service practitionerService;

    @JsonProperty(value = "config_server_service", required = true)
    private Service configServerService;

    @JsonProperty(value = "spring_cloud", required = true)
    private SpringCloud springCloud;
}
