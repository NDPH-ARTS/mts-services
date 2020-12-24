package uk.ac.ox.ndph.arts.practitiner_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.arts.practitiner_service.model.Person;

@RestController
@SpringBootApplication
public class PractitionerServiceApp {

	@Autowired
	public PractitionerServiceApp()
	{
		
	}

	@PostMapping(path = "/person", consumes = "application/json", produces = "application/json")
	public String person(@RequestBody Person person) {
		return "success";
	}
}
