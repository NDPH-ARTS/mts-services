package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

/**
 * Controller for practitioner endpoint of practitioner-service
 */
@RestController
public class PractitionerController {

    private static final String ENDPOINT_PATH = "/practitioner";
    private static final String APPLICATION_JSON = "application/json";

    private final EntityService entityService;

    /**
     *
     * @param entityService validate and save the practitioner
     */
    @Autowired
    public PractitionerController(EntityService entityService) {
        this.entityService = entityService;
    }

    /**
     *
     * @param practitioner
     * @return ResponseEntity
     */
    @PostMapping(path = ENDPOINT_PATH, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Response> practitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
    }
}