package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;

@Component
@Configuration
public class SiteConfig {

    private class Mts {
        @JsonProperty(value = "site", required = true)
        private SiteConfiguration site;
    }

    @JsonProperty(value = "mts", required = true)
    private Mts mts;
}
