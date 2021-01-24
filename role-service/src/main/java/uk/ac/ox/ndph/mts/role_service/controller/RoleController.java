package uk.ac.ox.ndph.mts.role_service.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleDTO;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;
import uk.ac.ox.ndph.mts.role_service.service.RoleService;

import javax.validation.Valid;


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
        return roleRepository.findAll(PageRequest.of(page, size));
    }

    @PostMapping
    public Role create(@Valid @RequestBody RoleDTO roleDto) {

        Role roleEntity = convertDtoToEntity(roleDto);
        return roleService.saveRole(roleEntity);
    }

    protected Role convertDtoToEntity(RoleDTO roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

}