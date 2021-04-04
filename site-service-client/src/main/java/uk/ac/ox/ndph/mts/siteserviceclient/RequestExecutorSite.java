package uk.ac.ox.ndph.mts.siteserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.ac.ox.ndph.mts.siteserviceclient.configuration.ClientRoutesConfig;
import uk.ac.ox.ndph.mts.siteserviceclient.exception.RestException;

import java.util.function.Consumer;
import java.util.function.Supplier;
@Component
public class RequestExecutorSite {

    private final Supplier<Retry> retryPolicy;
    private final ClientRoutesConfig clientRoutesConfig;

    @Autowired
    public RequestExecutorSite(Supplier<Retry> retryPolicy, ClientRoutesConfig clientRoutesConfig) {
        this.retryPolicy = retryPolicy;
        this.clientRoutesConfig = clientRoutesConfig;
    }

    protected <R, T> R sendBlockingPostRequest(WebClient webClient,
                                              String uri,
                                              T payload,
                                              Class<R> responseExpected,
                                              final Consumer<HttpHeaders> authHeaders) {
        return webClient.post()
                .uri(uri)
                .headers(authHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), payload.getClass())
                .retrieve()
                .onStatus(HttpStatus::isError,
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_PATH.format(
                                    clientRoutesConfig.getServiceName(), resp.statusCode(), uri))))

                .bodyToMono(responseExpected)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    protected <R> R sendBlockingGetRequest(WebClient webClient,
                                           String uri,
                                           Class<R> responseExpected,
                                           final Consumer<HttpHeaders> authHeaders) {
        return webClient.get()
                .uri(uri)
                .headers(authHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError,
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_PATH.format(
                                    clientRoutesConfig.getServiceName(), resp.statusCode(), uri))))
                .bodyToMono(responseExpected)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
