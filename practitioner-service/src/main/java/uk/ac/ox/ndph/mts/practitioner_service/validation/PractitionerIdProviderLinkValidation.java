package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerIdProviderLink;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

/**
 * Implements a ModelEntityValidation for PractitionerDirectoryLink
 */
@Component
public class PractitionerIdProviderLinkValidation implements ModelEntityValidation<PractitionerIdProviderLink> {

    @Autowired
    public PractitionerIdProviderLinkValidation() {
    }

    @Override
    public ValidationResponse validate(PractitionerIdProviderLink entity) {
        if (isNullOrBlank(entity.getPractitionerId())) {
            return new ValidationResponse(false, "practitionerId must have a value");
        }
        if (isNullOrBlank(entity.getUserAccountId())) {
            return new ValidationResponse(false, "directoryId must have a value");
        }

        return new ValidationResponse(true, "");
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

}
