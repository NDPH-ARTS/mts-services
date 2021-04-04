package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;

/**
 * Represents the init service configuration file. This class is meant to use as an object from which we compile
 * a json schema for validation
 */
@Component
@Configuration
public class InitTrialConfig {

    /**
     * Since the configuration schema starts with mts.trial but the config class
     * is annotated with a prefix which is NOT being translated into the
     * generated schema, we needed this inner class so we can force the generated
     * schema to match the actual structure.
     */
    private class Mts {
        @JsonProperty(value = "trial", required = true)
        private Trial trial;
    }

    @JsonIgnore
    public static final String SCHEMA_NAME = "init-service-trial-schema";

    @JsonProperty(value = "mts", required = true)
    private Mts mts;
}
