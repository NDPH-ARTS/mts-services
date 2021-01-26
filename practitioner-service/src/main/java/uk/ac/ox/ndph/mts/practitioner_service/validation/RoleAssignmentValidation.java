package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PageableResult;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

import java.util.stream.Stream;

/**
 * Implements a ModelEntityValidation for RoleAssignment
 */
@Component
public class RoleAssignmentValidation implements ModelEntityValidation<RoleAssignment> {

    @SuppressWarnings("FieldCanBeLocal")
    private final Logger logger = LoggerFactory.getLogger(RoleAssignmentValidation.class);

    private final WebClient webClient;
    private String roleService;

    @Autowired
    public RoleAssignmentValidation(final WebClient webClient, @Value("${role.service}") final String roleService) {
        this.webClient = webClient;
        this.roleService = roleService;
        logger.info(Validations.STARTUP.message(), "RoleAssignment", roleService);
    }

    @Override
    public ValidationResponse validate(RoleAssignment entity) {
        if (isNullOrBlank(entity.getPractitionerId())) {
            return new ValidationResponse(false, "practitionerId must have a value");
        }
        if (isNullOrBlank(entity.getSiteId())) {
            return new ValidationResponse(false, "siteId must have a value");
        }
        if (isNullOrBlank(entity.getRoleId())) {
            return new ValidationResponse(false, "roleId must have a value");
        }
        if (!isRole(entity.getRoleId())) {
            return new ValidationResponse(false, "roleId must refer to a valid role");
        }
        return new ValidationResponse(true, "");
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    private boolean isRole(final String roleId) {
        return roleId != null && getRoles().anyMatch(r -> r.getId().equals(roleId));
    }

    // dependency on RoleService
    // service discovery?
    private Stream<RoleDTO> getRoles() {
        return webClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl(this.roleService)
                        .path("/roles")
                        .queryParam("page", 0)
                        .queryParam("size", Integer.MAX_VALUE)
                        .build()
                        .toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageableResult<RoleDTO>>() {})
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .blockOptional()
                .orElseGet(PageableResult::empty)
                .stream()
                .filter(r -> r != null && r.getId() != null);
    }

}
