package uk.ac.ox.ndph.mts.practitioner_service.service;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerIdProviderLink;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

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
    private final ModelEntityValidation<PractitionerIdProviderLink> directoryLinkValidator;

    /**
     * @param practitionerStore Practitioner store interface
     * @param practitionerValidator Practitioner validation interface
     */
    @Autowired
    public PractitionerService(EntityStore<Practitioner> practitionerStore,
                               ModelEntityValidation<Practitioner> practitionerValidator,
                               EntityStore<RoleAssignment> roleAssignmentStore,
                               ModelEntityValidation<RoleAssignment> roleAssignmentValidator,
                               ModelEntityValidation<PractitionerIdProviderLink> directoryLinkValidator) {
        this.practitionerStore =
                Objects.requireNonNull(practitionerStore, "practitioner store cannot be null");
        this.practitionerValidator =
                Objects.requireNonNull(practitionerValidator, "practitioner entity validation cannot be null");
        this.directoryLinkValidator =
                Objects.requireNonNull(directoryLinkValidator, "directory link validation cannot be null");
        this.roleAssignmentStore =
                Objects.requireNonNull(roleAssignmentStore, "RoleAssignment store cannot be null");
        this.roleAssignmentValidator =
                Objects.requireNonNull(roleAssignmentValidator, "RoleAssignment entity validation cannot be null");
        
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

    @Override
    public Practitioner findPractitionerById(String id) throws ResponseStatusException {
        return practitionerStore
                .getEntity(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, Services.PRACTITIONER_NOT_FOUND.message()));

    }

    @Override
    public void linkPractitioner(PractitionerIdProviderLink link) {
        ValidationResponse validationResponse = directoryLinkValidator.validate(link);

        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
        
        final Practitioner practitioner = practitionerStore.getEntity(link.getPractitionerId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, Services.PRACTITIONER_NOT_FOUND.message()));
        practitioner.setUserAccountId(link.getUserAccountId());
        
        practitionerStore.saveEntity(practitioner);
    }
    
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
        return roleAssignmentStore.findEntitiesByUserIdentity(userIdentity);
    }
}
