package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import java.util.List;

/**
 * Implement an EntityStore for Practitioner.
 */
@Component
public class PractitionerStore implements EntityStore<Practitioner> {
    private FhirRepository repository;
    private EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> converter;

    /**
     *
     * @param repository - The fhir repository
     * @param converter - a model-entity to fhir-entity converter
     */
    @Autowired
    public PractitionerStore(FhirRepository repository,
            EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public String saveEntity(Practitioner entity) {
        return repository.savePractitioner(converter.convert(entity));
    }

    @Override
    public List<Practitioner> listEntities(String id) {
        throw new NotImplementedException();
    }


}
