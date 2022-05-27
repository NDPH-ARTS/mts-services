package uk.ac.ox.ndph.mts.init_service.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import uk.ac.ox.ndph.mts.init_service.model.Permission;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, String> {
}
