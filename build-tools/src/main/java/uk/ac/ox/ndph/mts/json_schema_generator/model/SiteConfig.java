package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.AddressConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;

/**
 * Represents the site service configuration file. This class is meant to use as an object from which we compile
 * a json schema for validation
 */
@Component
@Configuration
public class SiteConfig {

    private class Mts {
        @JsonProperty(value = "site", required = true)
        private SiteConfiguration site;

        @JsonProperty(value = "address")
        private AddressConfiguration address;
    }

    @JsonIgnore
    public static final String SCHEMA_NAME = "site-service-configuration-schema";

    @JsonProperty(value = "mts", required = true)
    private Mts mts;
}
