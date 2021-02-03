package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

/**
 * Implement an EntityStore for Practitioner.
 */
@Component
public class PractitionerStore implements EntityStore<Practitioner> {
    private FhirRepository repository;
    private EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> modelToFhirConverter;
    private EntityConverter<org.hl7.fhir.r4.model.Practitioner, Practitioner> fhirToModelConverter;

    /**
     *
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
    public uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner getEntity(String id) {
        return fhirToModelConverter.convert(repository.getPractitioner(id));
    }
    
    @Override
    public String saveEntity(Practitioner entity) {
        return repository.savePractitioner(modelToFhirConverter.convert(entity));
    }
}
