package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Implement an EntityService interface.
 * Validation of practitioner based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class PractitionerService implements EntityService {

    private final EntityStore<Practitioner> practitionerStore;
    private final ModelEntityValidation<Practitioner> practitionerValidator;
    @SuppressWarnings("FieldCanBeLocal")
    private final Logger logger = LoggerFactory.getLogger(PractitionerService.class);

    private final EntityStore<RoleAssignment> roleAssignmentStore;
    private final ModelEntityValidation<RoleAssignment> roleAssignmentValidator;

    /**
     * @param practitionerStore Practitioner store interface
     * @param practitionerValidator Practitioner validation interface
     */
    @Autowired
    public PractitionerService(EntityStore<Practitioner> practitionerStore,
                               ModelEntityValidation<Practitioner> practitionerValidator,
                               EntityStore<RoleAssignment> roleAssignmentStore,
                               ModelEntityValidation<RoleAssignment> roleAssignmentValidator) {
        Objects.requireNonNull(practitionerStore, "practitioner store cannot be null");
        Objects.requireNonNull(practitionerValidator, "practitioner entity validation cannot be null");
        Objects.requireNonNull(roleAssignmentStore, "RoleAssignment store cannot be null");
        Objects.requireNonNull(roleAssignmentValidator, "RoleAssignment entity validation cannot be null");
        this.practitionerStore = practitionerStore;
        this.practitionerValidator = practitionerValidator;
        this.roleAssignmentStore = roleAssignmentStore;
        this.roleAssignmentValidator = roleAssignmentValidator;
        logger.info(Services.STARTUP.message());
    }

    /**
     * @param practitioner the Practitioner to save.
     * @return The id of the new practitioner
     */
    public String savePractitioner(Practitioner practitioner) {
        var validationResponse = practitionerValidator.validate(practitioner);
        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
        return practitionerStore.saveEntity(practitioner);
    }

    //TODO: should this be on its own service?
    @Override
    public String saveRoleAssignment(RoleAssignment roleAssignment) {
        ValidationResponse validationResponse = roleAssignmentValidator.validate(roleAssignment);
        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
        return roleAssignmentStore.saveEntity(roleAssignment);
    }

    @Override
    public List<RoleAssignment> getRoleAssignmentsByUserIdentity(@NotNull String userIdentity) {
        Objects.requireNonNull(userIdentity, "User identifier can not be null.");
        return roleAssignmentStore.listEntitiesByUserIdentity(userIdentity);
    }
}
