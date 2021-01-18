package uk.ac.ox.ndph.mts.site_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.model.Site;

/**
 * Implement an EntityStore for Site / Organization.
 */
@Component
public class OrganizationStore implements EntityStore<Site> {


    private FhirRepository repository;
    private EntityConverter<Site, org.hl7.fhir.r4.model.Organization> converter;

    /**
     *
     * @param repository - The fhir repository
     * @param converter - a model-entity to fhir-entity converter
     */
    @Autowired
    public OrganizationStore(FhirRepository repository,
                             EntityConverter<Site, org.hl7.fhir.r4.model.Organization> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public String saveEntity(Site entity) {
        return repository.saveOrganization(converter.convert(entity));
    }
}
