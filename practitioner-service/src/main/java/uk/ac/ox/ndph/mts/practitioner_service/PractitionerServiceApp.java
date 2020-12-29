package uk.ac.ox.ndph.mts.practitioner_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

@RestController
@SpringBootApplication
public class PractitionerServiceApp {
	private static final String SUCCESS_STATUS = "success";
	private static final String ENDPOINT_PATH = "/practitioner";
	private static final String APPLICATION_JSON = "application/json";
	private static final String RESPONSE = "{\"id\": \"%s\"}";

	private EntityService entityService;

	@Autowired
	public PractitionerServiceApp(EntityService entityService) {
		this.entityService = entityService;
	}

	@PostMapping(path = ENDPOINT_PATH, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
	public ResponseEntity<String> practitioner(@RequestBody Practitioner practitioner) {
		String practitionerId = entityService.savePractitioner(practitioner);
		return ResponseEntity.status(HttpStatus.CREATED).body(String.format(RESPONSE, practitionerId));
	}
}
