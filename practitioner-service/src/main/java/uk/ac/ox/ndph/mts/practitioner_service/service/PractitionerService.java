package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

/**
 * Implement an EntityService interface.
 * Validation of practitioner based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class PractitionerService implements EntityService {

    private EntityStore<Practitioner> practitionerStore;
    private final ModelEntityValidation<Practitioner> entityValidation;
    private final Logger logger = LoggerFactory.getLogger(PractitionerService.class);

    /**
     *
     * @param practitionerStore Practitioner store interface
     * @param entityValidation Practitioner validation interface 
     */
    @Autowired
    public PractitionerService(EntityStore<Practitioner> practitionerStore,
            ModelEntityValidation<Practitioner> entityValidation) {
        if (practitionerStore == null) {
            throw new InitialisationError("practitioner store cannot be null");
        }
        if (entityValidation == null) {
            throw new InitialisationError("entity validation cannot be null");
        }
        this.practitionerStore = practitionerStore;
        this.entityValidation = entityValidation;
        logger.info(Services.STARTUP.message());
    }

    /**
     *
     * @param practitioner the Practitioner to save.
     * @return The id of the new practitioner
     */
    public String savePractitioner(Practitioner practitioner) {
        var validationResponse = entityValidation.validate(practitioner);
        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
        return practitionerStore.createEntity(practitioner);
    }
}
