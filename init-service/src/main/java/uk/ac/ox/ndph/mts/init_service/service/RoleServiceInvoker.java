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
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.Role;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceInvoker implements ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceInvoker.class);

    @Value("${role.service}")
    private String roleService;

    private final WebClient webClient;

    public RoleServiceInvoker() {
        this.webClient = WebClient.create(roleService);
    }
    public RoleServiceInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String send(Entity role) throws DependentServiceException {

        try {
            Role responseDataRole = webClient.post()
                    .uri(roleService + "/roles")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(role), Role.class)
                    .retrieve()
                    .bodyToMono(Role.class)
                    .block();
            return responseDataRole.getId();
        } catch (Exception e) {
            LOGGER.info("FAILURE roleService {}", e.getMessage());
            throw new DependentServiceException("Error connecting to role service");
        }
    }

    public void execute(List<Role> roles) throws NullEntityException {
        if (roles != null) {
            for (Role role : roles) {
                LOGGER.info("Starting to create role(s): {}", role);
                send(role);
                LOGGER.info("Finished creating {} role(s)", roles.size());
            }
        } else {
            throw new NullEntityException("No Roles in payload");
        }
    }
}
