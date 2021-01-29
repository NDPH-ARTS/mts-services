package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.client.RoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

/**
 * Implements a ModelEntityValidation for RoleAssignment
 */
@Component
public class RoleAssignmentValidation implements ModelEntityValidation<RoleAssignment> {

    private final RoleServiceClient roleServiceClient;

    @Autowired
    public RoleAssignmentValidation(final RoleServiceClient roleServiceClient) {
        this.roleServiceClient = roleServiceClient;
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
        if (!isRole(entity.getRoleId())) {
            return new ValidationResponse(false, "roleId must refer to a valid role");
        }
        return new ValidationResponse(true, "");
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    private boolean isRole(final String roleId) {
        return roleId != null && this.roleServiceClient.roleIdExists(roleId);
    }

}
