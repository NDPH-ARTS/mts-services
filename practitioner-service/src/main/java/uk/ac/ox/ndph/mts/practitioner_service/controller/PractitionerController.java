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

@RestController
public class PractitionerController {

    private static final String APPLICATION_JSON = "application/json";

    private final EntityService entityService;

    @Autowired
    public PractitionerController(EntityService entityService) {
        this.entityService = entityService;
    }

    @PostMapping(path = "/practitioner", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Response> practitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
    }

    @PostMapping(path = "/practitioner/link", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Response> practitionerLink(
            @RequestParam String userAccountId,
            @RequestParam String practitionerId) {
        entityService.linkPractitioner(userAccountId, practitionerId);
        // TODO (archiem) implement return value
//        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
        return null;
    }
}
