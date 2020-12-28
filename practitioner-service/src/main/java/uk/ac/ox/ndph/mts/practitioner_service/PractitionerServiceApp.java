package uk.ac.ox.ndph.arts.practitioner_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.arts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.arts.practitioner_service.service.EntityService;

@RestController
@SpringBootApplication
public class PractitionerServiceApp {

	private EntityService entityService;

	@Autowired
	public PractitionerServiceApp(EntityService entityService) {
		this.entityService = entityService;
	}

	@PostMapping(path = "/practitioner", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> practitioner(@RequestBody Practitioner practitioner) {
		String practitionerId = entityService.savePractitioner(practitioner);
		return ResponseEntity.status(HttpStatus.CREATED).body(String.format("{\"id\": \"%s\"}", practitionerId));
	}
}
