package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import java.util.List;
import java.util.Optional;

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

    Optional<Practitioner> getPractitioner(String id);

    String savePractitionerRole(PractitionerRole practitionerRole);

    List<PractitionerRole> getPractitionerRolesByUserIdentity(String userIdentity);

    List<Practitioner> getPractitionersByUserIdentity(String userIdentity);

    List<Practitioner> findAllPractitioners();
}
