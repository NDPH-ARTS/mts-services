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

    private final Logger log = LoggerFactory.getLogger(WebConfig.class);

    private static ExchangeFilterFunction logRequest(final Logger logger) {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (logger.isDebugEnabled()) {
                final StringBuilder sb = new StringBuilder("Request: \n");
                //append clientRequest method and url
                clientRequest
                    .headers()
                    .forEach((name, values) -> values.forEach(value -> sb.append(name).append("=").append("value")));
                logger.debug(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

    @Bean
    public WebClient webClient() {
        return WebClient
            .builder()
            //.filters(exchangeFilterFunctions -> exchangeFilterFunctions.add(logRequest(log)))
            .build();
    }


}
