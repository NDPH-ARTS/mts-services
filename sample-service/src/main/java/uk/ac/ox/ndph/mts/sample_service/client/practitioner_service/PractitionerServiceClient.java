package uk.ac.ox.ndph.mts.sample_service.client.practitioner_service;

import uk.ac.ox.ndph.mts.sample_service.client.dtos.AssignmentRoleDTO;

/**
 * Practitioner service client interface
 */
public interface PractitionerServiceClient {

    AssignmentRoleDTO[] getUserAssignmentRoles(String userId);

}
