package uk.ac.ox.ndph.mts.role_service.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;
import uk.ac.ox.ndph.mts.role_service.service.RoleService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleController(RoleRepository roleRepository, RoleService roleService, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
    }

    @GetMapping("")
    public Page<Role> getPaged(@RequestParam
                                int page,
                               @RequestParam
                                int size) {
        Logger.getAnonymousLogger().info("get paged");
        return roleRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Role getOneRole(@PathVariable String id) throws ResponseStatusException{
        Logger.getAnonymousLogger().info("get role id: "+id);
        Optional<Role> retrievedRole = roleRepository.findById(id);
        if(retrievedRole.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        return retrievedRole.get();
    }

    @PostMapping("")
    public Role createRole(@Valid @RequestBody RoleDTO roleDto) {

        Role roleEntity = convertDtoToEntity(roleDto, Role.class);
        return roleService.saveRole(roleEntity);
    }

    @PostMapping("/{id}/permissions")
    public Role updatePermissionsForRole(@PathVariable String id, @Valid @RequestBody List<PermissionDTO> permissionsDTOs) {

        List<Permission> permissionEntities = convertListOfDtosToEntities(permissionsDTOs, Permission.class);
        return roleService.updatePermissionsForRole(id, permissionEntities);
    }


    protected <D, T> D convertDtoToEntity(final T inputInstance, Class<D> outputClass) {
        return modelMapper.map(inputInstance, outputClass);
    }

    protected <D, T> List<D> convertListOfDtosToEntities(final Collection<T> inputInstanceList, Class<D> outputCLass) {
        return inputInstanceList.stream()
                .map(inputInstance -> convertDtoToEntity(inputInstance, outputCLass))
                .collect(Collectors.toList());
    }

}
