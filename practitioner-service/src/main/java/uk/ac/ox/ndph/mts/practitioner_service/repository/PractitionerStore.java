package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

@Component
public class PractitionerStore implements EntityStore<Practitioner> {

    private FhirRepository repository;
    private EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> converter;
    private final IdGenerator generator;

    @Autowired
    public PractitionerStore(FhirRepository repository,
                             EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> converter,
                             final IdGenerator generator) {
        this.repository = repository;
        this.converter = converter;
        this.generator = generator;
    }

    @Override
    public String createEntity(Practitioner entity) {
        return repository.createPractitioner(converter.convert(entity));
    }
}
