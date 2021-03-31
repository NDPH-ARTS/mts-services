package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;

/**
 * Implements a ModelEntityValidation for RoleAssignment
 */
@Component
public class RoleAssignmentValidation implements ModelEntityValidation<RoleAssignment> {

    private final RoleServiceClient roleServiceClient;
    private final SiteServiceClient siteServiceClient;
    private final SecurityContextUtil securityContextUtil;

    @Autowired
    public RoleAssignmentValidation(final RoleServiceClient roleServiceClient,
                                    final SiteServiceClient siteServiceClient, SecurityContextUtil securityContextUtil) {
        this.roleServiceClient = roleServiceClient;
        this.siteServiceClient = siteServiceClient;
        this.securityContextUtil = securityContextUtil;
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

        if (!this.roleServiceClient.entityIdExists(entity.getRoleId(), RoleServiceClient.noAuth())) {
            return new ValidationResponse(false,
                    String.format(Validations.EXTERNAL_ENTITY_NOT_EXIST_ERROR.message(), "roleId"));
        }

        if (!this.siteServiceClient.entityIdExists(entity.getSiteId(), SiteServiceClient.bearerAuth(securityContextUtil.getToken()))) {
            return new ValidationResponse(false,
                    String.format(Validations.EXTERNAL_ENTITY_NOT_EXIST_ERROR.message(), "siteId"));
        }

        return new ValidationResponse(true, "");
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

}
