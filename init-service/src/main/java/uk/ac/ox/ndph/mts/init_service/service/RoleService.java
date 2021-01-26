package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.Role;

import java.util.List;

@Service
public class RoleService implements EntityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    @Value("${role.service}")
    private String roleService;

    private final WebClient webClient;

    public RoleService() {
        this.webClient = WebClient.create(roleService);
    }
    public RoleService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Role send(Entity role) throws DependentServiceException {

        try {
            return webClient.post()
                    .uri(roleService + "/roles")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(role), Role.class)
                    .retrieve()
                    .bodyToMono(Role.class)
                    .block();
        } catch (Exception e) {
            LOGGER.info("FAILURE roleService " + e.getMessage());
            throw new DependentServiceException("Error connecting to role service");
        }
    }

    public void execute(List<Role> roles) throws DependentServiceException {
        if (roles != null) {
            for (Role role : roles) {
                send(role);
                LOGGER.info("Created: {}", role);
            }
        }
    }
}
