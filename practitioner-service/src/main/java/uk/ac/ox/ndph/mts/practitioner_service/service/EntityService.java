package uk.ac.ox.ndph.mts.practitioner_service.service;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

/**
 * Interface for validating and saving an entity
 */
public interface EntityService {

    /**
     * Validate and save a practitioner entity
     *
     * @param practitioner the Practitioner to save.
     * @return the id of the created entity.
     */
    String savePractitioner(Practitioner practitioner);

    void linkPractitioner(String userAccountId, String practitionerId);

    String saveRoleAssignment(RoleAssignment roleAssignment);
}
