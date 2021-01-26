package uk.ac.ox.ndph.mts.role_service.service;

import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.role_service.controller.DuplicateRoleException;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

@Service
public class RoleService {


    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role saveRole(Role role) throws DuplicateRoleException {

        if (roleRepository.existsById(role.getId())) {
            throw new DuplicateRoleException();
        }

        return roleRepository.save(role);
    }

}
