package uk.ac.ox.ndph.mts.practitioner_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.practitioner_service.model.Person;

@RestController
@SpringBootApplication
public class PractitionerServiceApp {

	private static final String SUCCESS_STATUS = "success";
	private static final String ENDPOINT_PATH = "success";
	private static final String APPLICATION_JSON = "success";

	// TODO: Complete implementation as part of user story in next PR.
	@PostMapping(path = ENDPOINT_PATH, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
	public String person(@RequestBody Person person) {
		return SUCCESS_STATUS;
	}
}
