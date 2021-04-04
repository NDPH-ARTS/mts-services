package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Trial Definition file. This class is meant to use as an object from which we compile
 * a json schema for validation
 */
public class TrialDefinitionFile {

    @JsonIgnore
    public static final String SCHEMA_NAME = "definition-schema";

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(value = "tenant_id", required = true)
    private String tenantId;

    @JsonProperty(value = "ui_client_id", required = true)
    private String uiClientId;

    @JsonProperty(required = true, defaultValue = "1")
    private int version;

    @JsonProperty(value = "site_service", required = true)
    private Service siteService;

    @JsonProperty(value = "role_service", required = true)
    private Service roleService;

    @JsonProperty(value = "practitioner_service", required = true)
    private Service practitionerService;

    @JsonProperty(value = "init_service", required = true)
    private Service initService;

    @JsonProperty(value = "spring_cloud", required = true)
    private SpringCloud springCloud;

    @JsonProperty(value = "spring_profile", required = true)
    private String springProfile;

    @JsonProperty(value = "spring_config_label", required = true)
    private String springConfigLabel;
}
