package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.validation.RoleAssignmentValidation;

@Configuration
public class WebConfig {

    @Bean
    public WebClient webClient() {
        return WebClient
            .builder()
            .build();
    }


}
