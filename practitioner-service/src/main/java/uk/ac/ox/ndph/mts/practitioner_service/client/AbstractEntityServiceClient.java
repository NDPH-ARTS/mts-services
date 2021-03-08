package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;

import java.util.Objects;
import java.util.logging.Logger;

public abstract class AbstractEntityServiceClient implements EntityServiceClient {

    protected WebClient webClient;
    protected String serviceUrlBase;
    protected String serviceExistsRoute;
    protected SecurityContextUtil securityContextUtil;



    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean entityIdExists(String id) throws RestException {
        Logger.getAnonymousLogger().info("entityIDexists:"+serviceExistsRoute+id+" token="+securityContextUtil.getToken());
        Objects.requireNonNull(id, "id must be non-null");
        return webClient.get()
                .uri(serviceExistsRoute, id)
                .header("Authorization", "Bearer " + securityContextUtil.getToken())
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.just(false);
                    } else if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                }).onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
