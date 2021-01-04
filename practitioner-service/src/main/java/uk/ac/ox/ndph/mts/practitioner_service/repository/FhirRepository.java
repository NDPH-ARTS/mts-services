package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Practitioner;

/**
 * Interface for a FHIR entity repository
 */
public interface FhirRepository {

    /**
     * save a practitioner entity to FHIR store
     *
     * @param practitioner the practitioner to save.
     * @return Nothing.
     */
    String savePractitioner(Practitioner practitioner);
}
