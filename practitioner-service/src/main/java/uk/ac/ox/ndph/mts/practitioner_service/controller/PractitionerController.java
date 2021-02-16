package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.practitioner_service.client.RoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.client.SiteServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Controller for practitioner endpoint of practitioner-service
 */
@RestController
@RequestMapping(path = "/practitioner", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PractitionerController {

    private final EntityService entityService;

    private SiteServiceClient siteServiceClient;

    private RoleServiceClient roleServiceClient;

    /**
     * @param entityService validate and save the practitioner
     */
    @Autowired
    public PractitionerController(EntityService entityService, SiteServiceClient siteServiceClient, RoleServiceClient roleServiceClient) {
        this.entityService = entityService;

        this.roleServiceClient = roleServiceClient;
        this.siteServiceClient = siteServiceClient;
    }

    /**
     * @param practitioner The practitioner to create
     * @return ResponseEntity
     */
    @PostMapping()
    public ResponseEntity<Response> savePractitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(CREATED).body(new Response(practitionerId));
    }

    @PostMapping(path = "/{practitionerId}/link")
    public ResponseEntity<Response> linkPractitioner(@PathVariable String practitionerId, 
                                                    @RequestBody PractitionerUserAccount link) {
        link.setPractitionerId(practitionerId);
        entityService.linkPractitioner(link);
        return ResponseEntity.status(OK).build();
    }
    
    @GetMapping(path = "/{id}")
    public ResponseEntity<Practitioner> findPractitionerById(@PathVariable String id) {
        return ResponseEntity.ok(entityService.findPractitionerById(id));
    }

    @GetMapping(path = "/test/{id}")
    public ResponseEntity<Boolean> testClientSite(@PathVariable String id) {
        return ResponseEntity.ok(siteServiceClient.entityIdExists(id));
    }

    @GetMapping(path = "/test2/{id}")
    public ResponseEntity<Boolean> testClientRole(@PathVariable String id) {
        return ResponseEntity.ok(roleServiceClient.entityIdExists(id));
    }

    @PostMapping(path = "/{practitionerId}/roles")
    public ResponseEntity<Response> saveRoleAssignment(@PathVariable String practitionerId,
                                                       @RequestBody RoleAssignment roleAssignment) {
        roleAssignment.setPractitionerId(practitionerId);
        String roleAssignmentId = entityService.saveRoleAssignment(roleAssignment);
        return ResponseEntity.status(CREATED).body(new Response(roleAssignmentId));
    }

    @GetMapping(path = "/roles")
    public ResponseEntity<List<RoleAssignment>> getRoleAssignments(
            @NotBlank @NotNull @RequestParam String userIdentity) {
        if (!StringUtils.hasText(userIdentity)) {
            throw new RestException("Required String parameter 'userIdentity' is blank");
        }

        List<RoleAssignment> roleAssignments = entityService.getRoleAssignmentsByUserIdentity(userIdentity);
        return ResponseEntity.ok(roleAssignments);
    }
}
