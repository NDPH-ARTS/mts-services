package uk.ac.ox.ndph.mts.role_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import javax.validation.Valid;


@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @GetMapping("")
    Page<Role> getPaged(@RequestParam
                                int page,
                        @RequestParam
                                int size) {
        return roleRepository.findAll(PageRequest.of(page, size));
    }

    @PostMapping
    Role create(@Valid @RequestBody Role role) {

        return roleRepository.save(role);
    }

}
