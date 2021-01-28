package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

/**
 * Implement an EntityStore for RoleAssignment.
 */
@Component
public class RoleAssignmentStore implements EntityStore<RoleAssignment> {


    private final FhirRepository repository;
    private final EntityConverter<RoleAssignment, org.hl7.fhir.r4.model.PractitionerRole> converter;

    /**
     * @param repository - The fhir repository
     * @param converter - a model-entity to fhir-entity converter
     */
    @Autowired
    public RoleAssignmentStore(FhirRepository repository,
                               EntityConverter<RoleAssignment, org.hl7.fhir.r4.model.PractitionerRole> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public String createEntity(RoleAssignment entity) {
        return repository.savePractitionerRole(converter.convert(entity));
    }
}
