package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerIdProviderLink;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.util.List;

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

    void linkPractitioner(PractitionerIdProviderLink link);

    String saveRoleAssignment(RoleAssignment roleAssignment);

    /**
     * Find practitioner by ID
     *
     * @param id practitioner ID to search for
     * @return practitioner if found
     * @throws ResponseStatusException if not found
     */
    Practitioner findPractitionerById(String id) throws ResponseStatusException;

    List<RoleAssignment> getRoleAssignmentsByUserIdentity(String userIdentity);
}
