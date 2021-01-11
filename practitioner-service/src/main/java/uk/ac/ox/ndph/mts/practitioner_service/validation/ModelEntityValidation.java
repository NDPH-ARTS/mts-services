package uk.ac.ox.ndph.mts.practitioner_service.validation;

import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

/**
 * Interface for entity validation
 */
public interface ModelEntityValidation<T> {
    
    /**
     * Check if an the entity is valid
     * @param entity - the entity to validate
     * @return ValidationReponse
     */
    ValidationResponse validate(T entity);
}
