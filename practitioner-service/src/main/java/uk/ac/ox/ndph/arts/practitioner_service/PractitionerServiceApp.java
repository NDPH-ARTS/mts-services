package uk.ac.ox.ndph.arts.practitioner_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.arts.practitioner_service.model.Person;
import uk.ac.ox.ndph.arts.practitioner_service.service.IEntityService;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;

@RestController
@SpringBootApplication
public class PractitionerServiceApp {

	private IEntityService entityService;

	@Autowired
	public PractitionerServiceApp(IEntityService entityService) {
		this.entityService = entityService;
	}

	@PostMapping(path = "/practitioner", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> person(@RequestBody Person person) {
		try {
			String personId = entityService.savePerson(person);
			return ResponseEntity.status(HttpStatus.CREATED).body(String.format("{\"id\": \"%s\"}", personId));
		} catch (HttpStatusException e) {
			return ResponseEntity.status(e.getHttpStatus()).body(String.format("{\"error\": \"%s\"}",e.getMessage()));

		}
	}
}
