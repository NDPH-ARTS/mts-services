package uk.ac.ox.ndph.mts.practitioner_service.validation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;

/**
 * Implements a ModelEntityValidation for PractitionerDirectoryLink
 */
@Component
public class PractitionerUserAccountValidation implements ModelEntityValidation<PractitionerUserAccount> {

    private EntityStore<Practitioner> practitionerStore;

    @Autowired
    public PractitionerUserAccountValidation(EntityStore<Practitioner> practitionerStore) {
        this.practitionerStore = practitionerStore;
    }

    @Override
    public ValidationResponse validate(PractitionerUserAccount userAccount) {

        if (!StringUtils.hasText(userAccount.getPractitionerId())) {
            return new ValidationResponse(false, "practitionerId must have a value");
        }
        if (!StringUtils.hasText(userAccount.getUserAccountId())) {
            return new ValidationResponse(false, "userAccountId must have a value");
        }

        Optional<Practitioner> practitioner = practitionerStore.getEntity(userAccount.getPractitionerId());
        
        if (!practitioner.isPresent()) {
            return new ValidationResponse(false, "Invalid participant id");
        }
        
        if (!practitionerStore.findEntitiesByUserIdentity(userAccount.getUserAccountId()).isEmpty()) {
            return new ValidationResponse(false, "This user account id is already linked to a practitioner.");
        }
        
        if (StringUtils.hasText(practitioner.get().getUserAccountId().trim())) {
            return new ValidationResponse(false, "This person already has a user account id registered.");
        }
        
        return new ValidationResponse(true, "");
    }
}
