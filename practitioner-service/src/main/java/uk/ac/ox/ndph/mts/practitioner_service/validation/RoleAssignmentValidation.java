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
        try {
            return roleId != null && getRoles().anyMatch(r -> r != null && r.getId() != null && r.getId().equals(roleId));
        } catch (Exception ex) {
            // log, throw, or just return false?
            logger.warn("Web client exception looking up role ID: " + roleId + " in " + roleService, ex);
            return false;   // fail safe
        }
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
                .blockOptional().orElseGet(PageableResult::empty).stream();
    }

}
