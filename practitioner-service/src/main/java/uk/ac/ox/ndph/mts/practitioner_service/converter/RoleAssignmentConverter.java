package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement an EntityConverter for RoleAssignment
 */
@Component
public class RoleAssignmentConverter implements EntityConverter<RoleAssignment,
        org.hl7.fhir.r4.model.PractitionerRole> {

    /**
     * Convert a RoleAssignment to an hl7 model PractitionerRole.
     *
     * @param input the MTS PractitionerRole to convert.
     * @return org.hl7.fhir.r4.model.PractitionerRole
     */
    @Override
    public org.hl7.fhir.r4.model.PractitionerRole convert(RoleAssignment input) {
        org.hl7.fhir.r4.model.PractitionerRole fhirPractitionerRole = new org.hl7.fhir.r4.model.PractitionerRole();
        fhirPractitionerRole.setOrganization(new Reference("Organization/" + input.getSiteId()));
        fhirPractitionerRole.setPractitioner(new Reference("Practitioner/" + input.getPractitionerId()));
        fhirPractitionerRole.addCode().setText(input.getRoleId());

        return fhirPractitionerRole;
    }

    @Override
    public List<org.hl7.fhir.r4.model.PractitionerRole> convertList(List<RoleAssignment> input){
        List<org.hl7.fhir.r4.model.PractitionerRole> practitionerRoles = new ArrayList<>();
        for (var roleAssignment: input) {
            practitionerRoles.add(convert(roleAssignment));
        }
        return  practitionerRoles;
    }
}
