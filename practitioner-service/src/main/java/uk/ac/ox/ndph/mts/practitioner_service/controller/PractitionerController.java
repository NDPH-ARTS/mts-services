package uk.ac.ox.ndph.mts.practitioner_service.controller;

//import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
//import uk.ac.ox.ndph.mts.practitioner_service.model.UserIdentity;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import static org.springframework.http.HttpStatus.CREATED;
//import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
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

    @PostMapping(path = "")
    public ResponseEntity<Response> savePractitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(CREATED).body(new Response(practitionerId));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Practitioner> findPractitionerById(@PathVariable String id) {
        return ResponseEntity.status(OK).body(entityService.findPractitionerById(id));
    }

    //    @PostMapping(path = "/{practitionerId}/link")
//    public ResponseEntity<Response> linkUserIdentity(@PathVariable String practitionerId,
//                                                     @RequestBody UserIdentity userIdentity) {
//        entityService.linkPractitioner(userIdentity, practitionerId);
//        return ResponseEntity.status(CREATED).build();
//    }

    @PostMapping(path = "/{practitionerId}/roles")
    public ResponseEntity<Response> saveRoleAssignment(@PathVariable String practitionerId,
                                                       @RequestBody RoleAssignment roleAssignment) {
        roleAssignment.setPractitionerId(practitionerId);
        String roleAssignmentId = entityService.saveRoleAssignment(roleAssignment);
        return ResponseEntity.status(CREATED).body(new Response(roleAssignmentId));
    }
}
