package uk.ac.ox.ndph.mts.sample_service.client.practitioner_service;

import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;


public interface PractitionerServiceClient {

    RoleAssignmentDTO[] getUserAssignmentRoles(String userId);
}
