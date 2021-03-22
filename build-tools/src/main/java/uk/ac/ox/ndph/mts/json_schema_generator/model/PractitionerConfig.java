package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;

@Component
@Configuration
public class PractitionerConfig {

    private class Mts {
        @JsonProperty(value = "practitioner", required = true)
        private PractitionerConfiguration practitioner;
    }

    @JsonProperty(value = "mts", required = true)
    private Mts mts;
}
