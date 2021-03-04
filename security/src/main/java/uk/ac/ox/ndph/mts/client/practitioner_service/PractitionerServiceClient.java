package uk.ac.ox.ndph.mts.client.practitioner_service;

import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;

import java.util.List;

/**
 * Practitioner service client interface
 */
public interface PractitionerServiceClient {

    List<RoleAssignmentDTO> getUserRoleAssignments(String userId, String token);

}
