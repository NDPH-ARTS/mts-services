package uk.ac.ox.ndph.mts.siteserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.function.Consumer;
import java.util.function.Supplier;
@Component
public class RequestExecutor {

    private final Supplier<Retry> retryPolicy;

    @Autowired
    public RequestExecutor(Supplier<Retry> retryPolicy) {
        this.retryPolicy = retryPolicy;
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
                .bodyToMono(responseExpected)
                .retryWhen(retryPolicy.get())
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
                .bodyToMono(responseExpected)
                .retryWhen(retryPolicy.get())
                .block();
    }
}
