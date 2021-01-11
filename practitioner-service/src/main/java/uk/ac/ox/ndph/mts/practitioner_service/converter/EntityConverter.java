package uk.ac.ox.ndph.mts.practitioner_service.converter;

/**
 * Convert from a data model entity to a FHIR entity type
 */
public interface EntityConverter<TModelEntity, TFhirEntity> {

    /**
     *
     * @param input - the entity to convert
     * @return the converted FHIR entity
     */
    TFhirEntity convert(TModelEntity input);
}
