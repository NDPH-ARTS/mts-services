package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.util.List;


public interface EntityService {


    String savePractitioner(Practitioner practitioner);

    void linkPractitioner(PractitionerUserAccount link);

    String saveRoleAssignment(RoleAssignment roleAssignment);

    Practitioner findPractitionerById(String id) throws ResponseStatusException;

    List<RoleAssignment> getRoleAssignmentsByUserIdentity(String userIdentity);

    List<Practitioner> getPractitionersByUserIdentity(String userIdentity);
}
