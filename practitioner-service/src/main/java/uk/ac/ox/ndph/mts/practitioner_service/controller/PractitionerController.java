package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class PractitionerController {

    private final EntityService entityService;

    @Autowired
    public PractitionerController(EntityService entityService) {
        this.entityService = entityService;
    }

    @PostMapping(path = "/practitioner")
    @Consumes(APPLICATION_JSON_VALUE)
    @Produces(APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> practitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
    }

    @PostMapping(path = "/practitioner/link")
    @Consumes(APPLICATION_JSON_VALUE)
    @Produces(APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> practitionerLink(
            @RequestParam String userAccountId,
            @RequestParam String practitionerId) {
        entityService.linkPractitioner(userAccountId, practitionerId);
        // TODO (archiem) implement return value
        // yes
//        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
        return null;
    }
}
