package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

/**
 * Implements a ModelEntityValidation for RoleAssignment
 */
@Component
public class RoleAssignmentValidation implements ModelEntityValidation<RoleAssignment> {

    @SuppressWarnings("FieldCanBeLocal")
    private final Logger logger = LoggerFactory.getLogger(RoleAssignmentValidation.class);

    @Autowired
    public RoleAssignmentValidation() {
        logger.info(Validations.STARTUP.message(), "RoleAssignment", "");
    }

    @Override
    public ValidationResponse validate(RoleAssignment entity) {
        if (isNullOrBlank(entity.getPractitionerId())) {
            return new ValidationResponse(false, "practitionerId must have a value");
        }
        if (isNullOrBlank(entity.getSiteId())) {
            return new ValidationResponse(false, "siteId must have a value");
        }
        if (isNullOrBlank(entity.getRoleId())) {
            return new ValidationResponse(false, "roleId must have a value");
        }

        return new ValidationResponse(true, "");
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

}
