package uk.ac.ox.ndph.arts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Practitioner;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;

/**
* Interface for a FHIR entity repository
*/
public interface IFhirRepository {

   /**
   * save a practitioner entity to FHIR store
   * @param practitioner the practitioner to save.
   * @return Nothing.
   * @exception HttpStatusException On error with FHIR store.
   * @see HttpStatusException
   */
    String savePractitioner(Practitioner practitioner) throws HttpStatusException;
}