package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement an EntityConverter for PractitionerRole
 */
@Component
public class PractitionerRoleConverter implements EntityConverter<org.hl7.fhir.r4.model.PractitionerRole,
        RoleAssignment> {

    /**
     * Convert a hl7 model PractitionerRole to a RoleAssignment.
     *
     * @param input the org.hl7.fhir.r4.model.PractitionerRole to convert.
     * @return MTS PractitionerRole
     */
    @Override
    public RoleAssignment convert(PractitionerRole input) {
        return new RoleAssignment(
                input.getPractitioner().getReferenceElement().getIdPart(),
                input.getOrganization().getReferenceElement().getIdPart(),
                input.getCode().get(0).getText());
    }

    @Override
    public List<RoleAssignment> convertList(List<PractitionerRole> input) {
        List<RoleAssignment> roleAssignments = new ArrayList<>();
        for (var practitionerRole: input) {
            roleAssignments.add(convert(practitionerRole));
        }
        return  roleAssignments;
    }
}
