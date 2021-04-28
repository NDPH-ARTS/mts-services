package uk.ac.ox.ndph.mts.init_service.repository;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.init_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;

/**
 * Implement an EntityStore for Practitioner.
 */
@Component
public class PractitionerStore implements EntityStore<Practitioner, String> {
    private final FhirRepository repository;
    private EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> modelToFhirConverter;

    /**
     * @param repository - The fhir repository
     * @param modelToFhirConverter - a model-entity to fhir-entity converter
     */
    @Autowired
    public PractitionerStore(FhirRepository repository,
                             EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> modelToFhirConverter) {
        this.repository = repository;
        this.modelToFhirConverter = modelToFhirConverter;
    }

    @Override
    public String save(Practitioner practitioner) {
        return repository.savePractitioner(modelToFhirConverter.convert(practitioner));
    }
    
    public String savePractitionerRole(final PractitionerRole practitionerRole) {
        return repository.savePractitionerRole(practitionerRole);
    }
    
}
