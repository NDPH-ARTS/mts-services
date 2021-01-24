package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

/**
 * Implement an EntityConverter for RoleAssignment
 */
@Component
public class RoleAssignmentConverter implements EntityConverter<RoleAssignment,
        org.hl7.fhir.r4.model.PractitionerRole> {

    /**
     * Convert an MTS PractitionerRole to an hl7 model PractitionerRole.
     *
     * @param input the MTS PractitionerRole to convert.
     * @return org.hl7.fhir.r4.model.PractitionerRole
     */
    public org.hl7.fhir.r4.model.PractitionerRole convert(RoleAssignment input) {
        org.hl7.fhir.r4.model.PractitionerRole fhirPractitionerRole = new org.hl7.fhir.r4.model.PractitionerRole();
        fhirPractitionerRole.setOrganization(new Reference("Organization/" + input.getSiteId()));
        fhirPractitionerRole.setPractitioner(new Reference("Practitioner/" + input.getPractitionerId()));
        fhirPractitionerRole.addCode().setText(input.getRoleId());

        return fhirPractitionerRole;
    }
}
