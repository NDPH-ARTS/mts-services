package uk.ac.ox.ndph.mts.practitioner_service.converter;

import java.util.List;

/**
 * Convert from a data model entity to a FHIR entity type
 */
public interface EntityConverter<T1, T2> {

    /**
     * @param input - the entity to convert
     * @return the converted FHIR entity
     */
    T2 convert(T1 input);
    List<T2> convertList(List<T1> input);
}
