package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.util.List;

/**
 * Implement an EntityStore for RoleAssignment.
 */
@Component
public class RoleAssignmentStore implements EntityStore<RoleAssignment> {


    private final FhirRepository repository;
    private final EntityConverter<RoleAssignment, PractitionerRole> roleAssignmentPractitionerRoleEntityConverter;
    private final EntityConverter<PractitionerRole, RoleAssignment> practitionerRoleRoleAssignmentEntityConverter;

    /**
     * @param repository - The fhir repository
     * @param roleAssignmentPractitionerRoleEntityConverter - a model-entity to fhir-entity converter
     * @param practitionerRoleRoleAssignmentEntityConverter - a fhir-entity to model-entity converter
     */
    @Autowired
    public RoleAssignmentStore(FhirRepository repository,
                               EntityConverter<RoleAssignment, PractitionerRole>
                                       roleAssignmentPractitionerRoleEntityConverter,
                               EntityConverter<PractitionerRole, RoleAssignment>
                                           practitionerRoleRoleAssignmentEntityConverter) {
        this.repository = repository;
        this.roleAssignmentPractitionerRoleEntityConverter = roleAssignmentPractitionerRoleEntityConverter;
        this.practitionerRoleRoleAssignmentEntityConverter = practitionerRoleRoleAssignmentEntityConverter;
    }

    @Override
    public String saveEntity(RoleAssignment entity) {
        return repository.savePractitionerRole(roleAssignmentPractitionerRoleEntityConverter.convert(entity));
    }

    @Override
    public List<RoleAssignment> listEntities(String id) {
        return practitionerRoleRoleAssignmentEntityConverter.convertList(
                repository.getPractitionerRolesByPractitionerIdentifier(id));
    }

}
