package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import javax.ws.rs.PathParam;
import java.util.List;

/**
 * Controller for practitioner endpoint of practitioner-service
 */
@RestController
@RequestMapping(path = "/practitioner", consumes = "application/json", produces = "application/json")
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
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
    }

    @PostMapping(path = "/{practitionerId}/roles")
    public ResponseEntity<Response> saveRoleAssignment(@PathVariable String practitionerId,
                                                       @RequestBody RoleAssignment roleAssignment) {
        roleAssignment.setPractitionerId(practitionerId);
        String roleAssignmentId = entityService.saveRoleAssignment(roleAssignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(roleAssignmentId));
    }

    @GetMapping(path = "/roles")
    @PathParam("identifier")
    public ResponseEntity<List<RoleAssignment>> getRoleAssignments(@PathParam("identifier") String identifier) {
        if (identifier == null || identifier.isEmpty()){
            throw new RestException("A required parameter: identifier is blank or missing.");
        }
        List<RoleAssignment> roleAssignments = entityService.getRoleAssignmentsByIdentifier(identifier);
        return  ResponseEntity.status(HttpStatus.OK).body(roleAssignments);
    }
}
