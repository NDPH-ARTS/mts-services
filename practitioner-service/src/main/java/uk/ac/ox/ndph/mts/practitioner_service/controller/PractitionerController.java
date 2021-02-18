package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for practitioner endpoint of practitioner-service
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/practitioner", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PractitionerController {

    private final EntityService entityService;

    /**
     * @param entityService validate and save the practitioner
     */
    @Autowired
    public PractitionerController(EntityService entityService) {
        this.entityService = entityService;
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

    @GetMapping(path = "/profile", consumes = MediaType.ALL_VALUE) // Overrides consumes=json at class level.
    public ResponseEntity<List<Practitioner>> profile(@RequestParam String userIdentity) { // Will come from token once auth in place
        Logger.getAnonymousLogger().info("Call to profile endpoint " );
        List<Practitioner> practitioners = entityService.getPractitionersByUserIdentity(userIdentity);
        return ResponseEntity.ok(practitioners);
    }

    // This won't be needed once authentication module is in place
    @RequestMapping(value = "/profile", method = RequestMethod.OPTIONS, consumes = MediaType.ALL_VALUE)
    public ResponseEntity options(HttpServletResponse response, @RequestParam String userIdentity) {
        Logger.getAnonymousLogger().info("Call to OPTIONS " );
        response.setHeader("Allow", "GET,OPTIONS");
        return new ResponseEntity(HttpStatus.OK);
    }
}
