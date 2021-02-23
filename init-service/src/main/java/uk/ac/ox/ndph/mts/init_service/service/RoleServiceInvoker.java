package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.Role;

@Service
public class RoleServiceInvoker extends ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceInvoker.class);

    @Value("${role-service.uri}")
    private String roleService;

    @Value("${role-service.routes.create}")
    private String createEndpoint;

    public RoleServiceInvoker() {
    }

    public RoleServiceInvoker(WebClient webClient) {
        super(webClient);
    }

    @Override
    protected String create(Entity role) throws DependentServiceException {

        String uri = roleService + createEndpoint;
        Role returnedRole = sendBlockingPostRequest(uri, role, Role.class);
        return returnedRole.getId();
    }

}
