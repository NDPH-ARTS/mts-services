package uk.ac.ox.ndph.arts.practitioner_service.service;

import uk.ac.ox.ndph.arts.practitioner_service.model.Person;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;

/**
* Interface for validating and saving an entity
*/
public interface IEntityService {

    /**
   * Validate and save a person entity
   * @param person the person to save.
   * @return the id of the created entity.
   * @exception HttpStatusException On error upon validation or with dependant services.
   * @see HttpStatusException
   */
    String savePerson(Person person) throws HttpStatusException;
}