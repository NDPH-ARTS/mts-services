package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Practitioner;

public interface FhirRepository {

    String createPractitioner(Practitioner practitioner);

}
