package uk.ac.ox.ndph.mts.init_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.ox.ndph.mts.init_service.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
}
