package uk.ac.ox.ndph.mts.practitioner_service.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

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

    @PostMapping(path = "")
    @Consumes(APPLICATION_JSON_VALUE)
    @Produces(APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> savePractitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(CREATED).body(new Response(practitionerId));
    }

    @PostMapping(path = "/link")
    @Consumes(APPLICATION_JSON_VALUE)
    @Produces(APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> practitionerLink(
            @RequestParam String userAccountId,
            @RequestParam String practitionerId) {
        entityService.linkPractitioner(userAccountId, practitionerId);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Practitioner> getPractitioner(@PathVariable String id) {
        var practitioner = entityService.getPractitioner(id);
        if (practitioner == null) {
            return ResponseEntity.status(NOT_FOUND).body(null);
        }
        return ResponseEntity.status(OK).body(practitioner);
    }

    @PostMapping(path = "/{practitionerId}/roles")
    public ResponseEntity<Response> saveRoleAssignment(@PathVariable String practitionerId,
            @RequestBody RoleAssignment roleAssignment) {
        roleAssignment.setPractitionerId(practitionerId);
        String roleAssignmentId = entityService.saveRoleAssignment(roleAssignment);
        return ResponseEntity.status(CREATED).body(new Response(roleAssignmentId));
    }
}
