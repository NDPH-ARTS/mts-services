package uk.ac.ox.ndph.mts.role_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;


@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleController (RoleRepository roleRepository){
        this.roleRepository=roleRepository;
    }


    @GetMapping("")
    Page<Role> getPaged(
            @RequestParam
                    int page,
            @RequestParam
                    int size) {
        return roleRepository.findAll(PageRequest.of(page, size));
    }

    @PostMapping
    Role create(@RequestBody Role role) {

        return roleRepository.save(role);
    }

}
