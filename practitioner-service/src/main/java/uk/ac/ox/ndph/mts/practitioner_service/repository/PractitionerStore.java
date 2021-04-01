package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import java.util.List;
import java.util.Optional;

/**
 * Implement an EntityStore for Practitioner.
 */
@Component
public class PractitionerStore implements EntityStore<Practitioner> {
    private final FhirRepository repository;
    private final EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> modelToFhirConverter;
    private final EntityConverter<org.hl7.fhir.r4.model.Practitioner, Practitioner> fhirToModelConverter;

    /**
     * @param repository - The fhir repository
     * @param modelToFhirConverter - a model-entity to fhir-entity converter
     */
    @Autowired
    public PractitionerStore(FhirRepository repository,
                             EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> modelToFhirConverter,
                             EntityConverter<org.hl7.fhir.r4.model.Practitioner, Practitioner> fhirToModelConverter) {
        this.repository = repository;
        this.modelToFhirConverter = modelToFhirConverter;
        this.fhirToModelConverter = fhirToModelConverter;
    }

    @Override
    public Optional<Practitioner> getEntity(String practitionerId) {
        return repository.getPractitioner(practitionerId)
                .map(fhirToModelConverter::convert);
    }

    public List<Practitioner> getAll() {
        return fhirToModelConverter.convertList(repository.findAllPractitioners());
    }

    @Override
    public String saveEntity(Practitioner practitioner) {
        return repository.savePractitioner(modelToFhirConverter.convert(practitioner));
    }

    @Override
    public List<Practitioner> findEntitiesByUserIdentity(String userIdentity) {
        return fhirToModelConverter.convertList(repository.getPractitionersByUserIdentity(userIdentity));
    }
}
