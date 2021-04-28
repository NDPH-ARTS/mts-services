package uk.ac.ox.ndph.mts.init_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.ResearchStudy;

/**
 * Interface for a FHIR entity repository
 */
public interface FhirRepository {

    String saveOrganization(Organization organization);
    
    String savePractitioner(Practitioner practitioner);

    String savePractitionerRole(PractitionerRole practitionerRole);

    String saveResearchStudy(ResearchStudy researchStudy);

}
