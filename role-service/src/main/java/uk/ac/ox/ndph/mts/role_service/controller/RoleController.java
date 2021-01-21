package uk.ac.ox.ndph.mts.role_service.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;
import uk.ac.ox.ndph.mts.role_service.service.RoleService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


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
    public Role getOneRole(@PathVariable String id) {
        Logger.getAnonymousLogger().info("get role id: "+id);
        Optional<Role> retrievedRole = roleRepository.findById(id);
        if(retrievedRole.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        return retrievedRole.get();
    }

    @PostMapping("")
    public Role createRole(@Valid @RequestBody RoleDTO roleDto) {

        Role roleEntity = convertDtoToEntity(roleDto);
        return roleService.saveRole(roleEntity);
    }





    protected Role convertDtoToEntity(RoleDTO roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

}
