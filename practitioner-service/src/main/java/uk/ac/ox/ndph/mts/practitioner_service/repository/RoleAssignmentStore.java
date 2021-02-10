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
    private final EntityConverter<RoleAssignment, PractitionerRole> roleAssignmentPractitionerRoleConverter;
    private final EntityConverter<PractitionerRole, RoleAssignment> practitionerRoleRoleAssignmentConverter;

    /**
     * @param repository - The fhir repository
     * @param roleAssignmentPractitionerRoleConverter - a model-entity to fhir-entity converter
     * @param practitionerRoleRoleAssignmentConverter - a fhir-entity to model-entity converter
     */
    @Autowired
    public RoleAssignmentStore(FhirRepository repository,
                               EntityConverter<RoleAssignment, PractitionerRole>
                                       roleAssignmentPractitionerRoleConverter,
                               EntityConverter<PractitionerRole, RoleAssignment>
                                       practitionerRoleRoleAssignmentConverter) {
        this.repository = repository;
        this.roleAssignmentPractitionerRoleConverter = roleAssignmentPractitionerRoleConverter;
        this.practitionerRoleRoleAssignmentConverter = practitionerRoleRoleAssignmentConverter;
    }

    @Override
    public String saveEntity(RoleAssignment entity) {
        return repository.savePractitionerRole(roleAssignmentPractitionerRoleConverter.convert(entity));
    }

    @Override
    public List<RoleAssignment> findEntitiesByUserIdentity(String userIdentity) {
        return practitionerRoleRoleAssignmentConverter.convertList(
                repository.getPractitionerRolesByUserIdentity(userIdentity));
    }

}
