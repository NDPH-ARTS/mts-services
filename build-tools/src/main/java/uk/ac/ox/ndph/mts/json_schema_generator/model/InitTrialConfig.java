package uk.ac.ox.ndph.mts.json_schema_generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;

@Component
@Configuration
public class InitTrialConfig {

    private class Mts {
        @JsonProperty(value = "trial", required = true)
        private Trial trial;
    }

    @JsonProperty(value = "mts", required = true)
    private Mts mts;
}
