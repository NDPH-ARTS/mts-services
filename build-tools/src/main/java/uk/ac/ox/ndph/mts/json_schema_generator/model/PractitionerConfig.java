package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;

/**
 * Represents the practitioner service configuration file. This class is meant to use as an object from which we compile
 * a json schema for validation
 */
@Component
@Configuration
public class PractitionerConfig {

    /**
     * Since the configuration schema starts with mts.practioioner but the config class
     * is annotated with a prefix which is NOT being translated into the
     * generated schema, we needed this inner class so we can force the generated
     * schema to match the actual structure.
     */
    private class Mts {
        @JsonProperty(value = "practitioner", required = true)
        private PractitionerConfiguration practitioner;
    }

    @JsonIgnore
    public static final String SCHEMA_NAME = "practitioner-service-configuration-schema";

    @JsonProperty(value = "mts", required = true)
    private Mts mts;
}
