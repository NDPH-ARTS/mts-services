package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import java.util.List;
import java.util.Optional;


public interface FhirRepository {


    String savePractitioner(Practitioner practitioner);

    Optional<Practitioner> getPractitioner(String id);

    String savePractitionerRole(PractitionerRole practitionerRole);

    List<PractitionerRole> getPractitionerRolesByUserIdentity(String userIdentity);

    List<Practitioner> getPractitionersByUserIdentity(String userIdentity);
}
