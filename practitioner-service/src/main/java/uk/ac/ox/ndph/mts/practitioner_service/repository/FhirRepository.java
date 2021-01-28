package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;

public interface FhirRepository {

    String createPractitioner(Practitioner practitioner, String personId);

    String savePractitionerRole(PractitionerRole practitionerRole);

}
