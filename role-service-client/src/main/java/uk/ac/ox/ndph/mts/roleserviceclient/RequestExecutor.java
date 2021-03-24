package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;
import uk.ac.ox.ndph.mts.roleserviceclient.model.Entity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
@Component
public class RequestExecutor {

    private final Supplier<Retry> retryPolicy;


    @Autowired
    public RequestExecutor(Supplier<Retry> retryPolicy) {
        this.retryPolicy = retryPolicy;
        }


    protected <R,T> R sendBlockingPostRequest(WebClient webClient, String uri, List<T> payload,
                                            Class<R> responseExpected,
                                            final Consumer<HttpHeaders> authHeaders) {
        return webClient.post()
                .uri(uri)
                .headers(authHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), payload.getClass())
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        resp -> Mono.error(new RestException(
                                ResponseMessages.SERVICE_NAME_STATUS_AND_PATH.format(
                                        "role-service", resp.statusCode(), uri))))

                .bodyToMono(responseExpected)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    protected <R> R sendBlockingGetRequest(WebClient webClient, String uri,
                                           Class<R> responseExpected,
                                           final Consumer<HttpHeaders> authHeaders) {
        return webClient.get()
                .uri(uri)
                .headers(authHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        resp -> Mono.error(new RestException(
                                ResponseMessages.SERVICE_NAME_STATUS_AND_PATH.format(
                                        "role-service", resp.statusCode(), uri))))
                .bodyToMono(responseExpected)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
