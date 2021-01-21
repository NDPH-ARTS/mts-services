package uk.ac.ox.ndph.mts.role_service.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.role_service.model.*;
import uk.ac.ox.ndph.mts.role_service.service.RoleService;

import javax.validation.Valid;
import java.util.Optional;
import java.util.logging.Logger;


@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    @GetMapping("")
    public Page<Permission> getPaged(@RequestParam
                                int page,
                                     @RequestParam
                                int size) {
        return permissionRepository.findAll(PageRequest.of(page, size));
    }


}
