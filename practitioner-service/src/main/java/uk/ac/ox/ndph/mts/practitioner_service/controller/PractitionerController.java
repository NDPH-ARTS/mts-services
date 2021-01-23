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

/**
 * Controller for practitioner endpoint of practitioner-service
 */
@RestController
public class PractitionerController {

    public static final String PARAM_USER_ACCOUNT_ID = "userAccountId";
    public static final String PARAM_PRACTITIONER_ID = "practitionerId";
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
    @PostMapping(path = "/practitioner", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Response> practitioner(@RequestBody Practitioner practitioner) {
        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
    }

    @PostMapping(path = "/practitioner/link", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Response> practitionerLink(@RequestParam(name = PARAM_USER_ACCOUNT_ID) String userAccountId,
                                                     @RequestParam(name = PARAM_PRACTITIONER_ID) String practitionerId) {
        entityService.linkPractitioner(userAccountId, practitionerId);
        // TODO (archiem) implement return value
//        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(practitionerId));
        return null;
    }
}
