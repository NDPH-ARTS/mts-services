package uk.ac.ox.ndph.mts.site_service.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.practitionerserviceclient.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.security.authorisation.AuthorisationService;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAddress;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteDTO;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteAddressDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implement an SiteServiceInterface interface.
 * Validation of site based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class SiteServiceImpl implements SiteService {

    private final Map<String, SiteConfiguration> sitesByType = new HashMap<>();
    private final Map<String, String> parentTypeByChildType = new HashMap<>();
    private final EntityStore<Site, String> siteStore;
    private final ModelEntityValidation<Site> entityValidation;
    private final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);
    private final SiteUtil siteUtil;
    private final AuthorisationService authService;
    private final RoleServiceClient roleServClnt;
    private final PractitionerServiceClient practServClnt;

    /**
     * @param configuration    injected site configuration
     * @param siteStore        Site store interface
     * @param entityValidation Site validation interface
     * @param siteUtil
     * @param authService
     * @param roleServClnt
     * @param practServClnt
     */
    @Autowired
    public SiteServiceImpl(final SiteConfiguration configuration,
                           final EntityStore<Site, String> siteStore,
                           final ModelEntityValidation<Site> entityValidation,
                           SiteUtil siteUtil, AuthorisationService authService,
                           RoleServiceClient roleServClnt,
                           PractitionerServiceClient practServClnt) {
        this.siteUtil = siteUtil;
        this.authService = authService;
        this.roleServClnt = roleServClnt;
        this.practServClnt = practServClnt;
        Objects.requireNonNull(configuration, "site configuration cannot be null");
        Objects.requireNonNull(siteStore, "site store cannot be null");
        Objects.requireNonNull(entityValidation, "entity validation cannot be null");
        this.siteStore = siteStore;
        this.entityValidation = entityValidation;
        addTypesToMap(configuration);
        logger.info(Services.STARTUP.message());
    }

    private void addTypesToMap(final SiteConfiguration configuration) {
        this.sitesByType.put(configuration.getType(), configuration);
        for (final var childConfig : CollectionUtils.emptyIfNull(configuration.getChild())) {
            addTypesToMap(childConfig);
            this.parentTypeByChildType.put(childConfig.getType(), configuration.getType());
        }
    }

    /**
     * @param site the Site to save.
     * @return The id of the new site
     */
    @Override
    public String save(final Site site) {
        var validationCoreAttributesResponse = entityValidation.validateCoreAttributes(site);
        var validationCustomAttributesResponse = entityValidation.validateCustomAttributes(site);

        String siteTypeForSite = StringUtils.defaultString(site.getSiteType());
        if (!validationCoreAttributesResponse.isValid()) {
            throw new ValidationException(validationCoreAttributesResponse.getErrorMessage());
        }

        if (!validationCustomAttributesResponse.isValid()) {
            throw new ValidationException(validationCustomAttributesResponse.getErrorMessage());
        }

        if (siteStore.existsByName(site.getName())) {
            throw new ValidationException(Services.SITE_NAME_EXISTS.message());
        }

        if (site.getParentSiteId() == null) {
            if (isRootSitePresent()) {
                throw new ValidationException(Services.ROOT_SITE_EXISTS.message());
            } else {
                if (!sitesByType.containsKey(siteTypeForSite) || parentTypeByChildType.containsKey(siteTypeForSite)) {
                    throw new ValidationException(Services.INVALID_ROOT_SITE.message());
                }
            }
        } else {
            Site siteParent = findSiteById(site.getParentSiteId());
            String siteParentType = siteParent.getSiteType();
            String allowedParentType = parentTypeByChildType.get(siteTypeForSite);
            if (!siteParentType.equalsIgnoreCase(allowedParentType)) {
                throw new ValidationException(Services.INVALID_CHILD_SITE_TYPE.message());
            }
        }
        return siteStore.saveEntity(site);
    }

    /**
     * Get complete sites list.  Note the list should never be empty if the trial has been initialized, and
     * this method should not be called if the trial has not been initialized. So throws an exception if the
     * store returns an empty sites list.
     *
     * @return list of sites, never empty
     * @throws InvariantException if the list from the store is empty
     */
    @Override
    public List<SiteDTO> findSites() {
        final List<SiteDTO> sites = this.siteStore.findAll().stream()
            .map(site -> convertSite(site))
            .collect(Collectors.toList());
        if (sites.isEmpty()) {
            throw new InvariantException(Services.NO_ROOT_SITE.message());
        }
        return sites;
    }

    /**
     * Find site by ID
     *
     * @param id site ID to search for
     * @return site if found
     * @throws ResponseStatusException if not found
     */
    public Site findSiteById(String id) throws ResponseStatusException {
        return this.siteStore
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, Services.SITE_NOT_FOUND.message()));

    }

    @Override
    public List<String> findParentSiteIds(String siteId) {
        return siteUtil.getParentSiteIds(siteId, findSites());
    }

    /**
     * Test if the root node is present. If not, then the site service cannot be safely used,
     * since the trial has not yet been initialized.
     *
     * @return true if a root site node is present
     */
    private boolean isRootSitePresent() {
        return this.siteStore
                .findRoot()
                .isPresent();
    }

    /**
     * Return the root node, throwing if not present
     *
     * @return site if found
     * @throws ResponseStatusException if no root site (bad trial initialization)
     */
    Site findRootSite() throws ResponseStatusException {
        return this.siteStore
                .findRoot()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Services.NO_ROOT_SITE.message()));
    }

    private SiteDTO convertSite(Site site) {
        SiteDTO siteDTO = new SiteDTO(site.getSiteId(), site.getParentSiteId());
        if (Objects.nonNull(site.getAddress())) {
            siteDTO.setAddress(convertAddress(site.getAddress()));
        }
        siteDTO.setSiteType(site.getSiteType());
        siteDTO.setAlias(site.getAlias());
        siteDTO.setName(site.getName());

        return siteDTO;
    }

    private SiteAddressDTO convertAddress(SiteAddress siteAddress) {
        return new SiteAddressDTO(siteAddress.getAddress1(), siteAddress.getAddress2(), siteAddress.getAddress3(),
            siteAddress.getAddress4(), siteAddress.getAddress5(), siteAddress.getCity(), siteAddress.getCountry(),
            siteAddress.getPostcode());
    }

    @Override
    public boolean filterUserSites(List<SiteDTO> sitesReturnObject,
                                   String userRole, String accessPerm) {
        try {
            Objects.requireNonNull(sitesReturnObject, "sites can not be null");

            //get unfiltered roleAssignments
            List<RoleAssignmentDTO> roleAssnmts = new ArrayList<>(
                practServClnt.getUserRoleAssignments(authService.getUserId(), authService.getAuthHeaders()));

            //return 403 if RAs is empty
            if (roleAssnmts == null || roleAssnmts.isEmpty()) {
                logger.info("User with id {} has no role assignments and therefore is unauthorised.",
                    authService.getUserId());
                return false;
            }

            //only keep the assigned sites (or child assigned sites)
            removeUnassignedSites(roleAssnmts, sitesReturnObject);

            //If we dont have receive a role then return the assigned sites
            if (userRole == null) {
                return true;
            }

            //Remove RAs if they dont match the role
            roleAssnmts.removeIf(ra -> !ra.getRoleId().equalsIgnoreCase(userRole));

            //If RAs are empty return 403
            if (roleAssnmts == null || roleAssnmts.isEmpty()) {
                logger.info("User with id {} has no role assignments and therefore is unauthorised.",
                    authService.getUserId());
                return false;
            }

            //Filter sites by role
            removeUnassignedSites(roleAssnmts, sitesReturnObject);

            //Get the RAs with the given permission - if they are then empty return 403
            List<RoleAssignmentDTO> roleAsstsWtPerm = getRoleAssmntsWtPerm(accessPerm, roleAssnmts);
            if (roleAsstsWtPerm.isEmpty()) {
                return false;
            }

            //Filter sites with perm based RAs
            //check if user has site (or child site) assigned
            removeUnassignedSites(roleAsstsWtPerm, sitesReturnObject);

            //If sites are empty return 403 (false)
            return !sitesReturnObject.isEmpty();
        } catch (Exception e) {
            return false;
        }

    }

    private void removeUnassignedSites(List<RoleAssignmentDTO> roleAssignments, List<SiteDTO> sites) {
        sites.removeIf(site ->
            !siteUtil.getUserSites(sites, roleAssignments)
                .contains(site.getSiteId()));
    }

    /**
     * Validate if the role assignments have the required permission linked to them.
     * @param reqPerm action required permission
     * @param roleAssmnts user role assignments
     * @return true if required permission is present in one of the roles
     */
    private List<RoleAssignmentDTO> getRoleAssmntsWtPerm(String reqPerm,
                                                         List<RoleAssignmentDTO> roleAssmnts) {

        Page<RoleDTO> roleDTOs = roleServClnt.getPage(0, 500,
            RoleServiceClient.bearerAuth(authService.getToken()));

        Set<String> rolesWithPermission = roleDTOs.stream()
            .filter(roleDto -> hasReqPermInRole(roleDto, reqPerm))
            .map(RoleDTO::getId)
            .collect(Collectors.toSet());

        return roleAssmnts.stream()
            .filter(roleAssignment -> rolesWithPermission.contains(roleAssignment.getRoleId()))
            .collect(Collectors.toList());
    }

    private boolean hasReqPermInRole(RoleDTO role, String requiredPermission) {
        return role.getPermissions().stream()
            .anyMatch(permission -> permission.getId().equals(requiredPermission));
    }

}
