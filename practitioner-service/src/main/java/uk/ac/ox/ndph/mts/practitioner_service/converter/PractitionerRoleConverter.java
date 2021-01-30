package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
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
                extractReferenceId(input.getPractitioner()),
                extractReferenceId(input.getOrganization()),
                input.getCode().get(0).getText());
    }

    private String extractReferenceId(Reference reference) {
        var id = reference.getReference();
        id = id.substring(id.lastIndexOf('/') + 1);
        return id;
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
