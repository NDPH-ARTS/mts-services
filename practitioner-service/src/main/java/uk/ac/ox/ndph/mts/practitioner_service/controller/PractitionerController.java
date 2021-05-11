package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.Response;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Controller for practitioner endpoint of practitioner-service
 */
@RestController
@RequestMapping(path = "/practitioner")
public class PractitionerController {

    private final EntityService entityService;
    private final SecurityContextUtil securityContextUtil;

    /**
     * @param entityService validate and save the practitioner
     */
    @Autowired
    public PractitionerController(EntityService entityService,  SecurityContextUtil securityContextUtil) {
        this.entityService = entityService;
        this.securityContextUtil = securityContextUtil;
    }

    /**
     * @param practitioner The practitioner to create
     * @return ResponseEntity
     */
    @PreAuthorize("@authorisationService.authorise('create-person')") //NOSONAR
    @PostMapping()
    public ResponseEntity<Response> savePractitioner(@RequestBody Practitioner practitioner) {

        String practitionerId = entityService.savePractitioner(practitioner);
        return ResponseEntity.status(CREATED).body(new Response(practitionerId));
    }

    @PreAuthorize("@authorisationService.authorise('link-user')") //NOSONAR
    @PostMapping(path = "/{practitionerId}/link")
    public ResponseEntity<Response> linkPractitioner(@PathVariable String practitionerId,
                                                     @RequestBody PractitionerUserAccount link) {
        link.setPractitionerId(practitionerId);
        entityService.linkPractitioner(link);
        return ResponseEntity.status(OK).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Practitioner> findPractitionerById(@PathVariable String id) {
        return ResponseEntity.ok(entityService.findPractitionerById(id));
    }

    @PreAuthorize("@authorisationService.authorise('assign-role', #roleAssignment.siteId)") //NOSONAR
    @PostMapping(path = "/{practitionerId}/roles")
    public ResponseEntity<Response> saveRoleAssignment(@PathVariable String practitionerId,
                                                       @RequestBody RoleAssignment roleAssignment) {
        roleAssignment.setPractitionerId(practitionerId);
        String roleAssignmentId = entityService.saveRoleAssignment(roleAssignment);
        return ResponseEntity.status(CREATED).body(new Response(roleAssignmentId));
    }

    @PreAuthorize("@authorisationService.authUserRoles(#userIdentity)")
    @GetMapping(path = "/roles")
    public List<RoleAssignment> getRoleAssignments(
            @NotBlank @NotNull @RequestParam String userIdentity) {
        if (!StringUtils.hasText(userIdentity)) {
            throw new RestException("Required String parameter 'userIdentity' is blank");
        }

        return entityService.getRoleAssignmentsByUserIdentity(userIdentity);
    }

    @GetMapping(path = "/profile", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<List<Practitioner>> profile() {
        String userIdFromToken = securityContextUtil.getUserId();
        List<Practitioner> practitioners = entityService.getPractitionersByUserIdentity(userIdFromToken);
        return ResponseEntity.ok(practitioners);
    }
}
