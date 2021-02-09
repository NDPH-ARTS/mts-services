package uk.ac.ox.ndph.mts.init_service.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;

public abstract class ServiceInvoker {

    protected abstract String create(Entity entity) throws DependentServiceException;

    private WebClient webClient;

    protected ServiceInvoker() {
        this.webClient = WebClient.create();
    }
    protected ServiceInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    protected <R> R sendBlockingPostRequest(String uri, Entity payload, Class<R> responseExpected) {
        return webClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(payload), payload.getClass())
                .retrieve()
                .bodyToMono(responseExpected)
                .block();
    }
}
