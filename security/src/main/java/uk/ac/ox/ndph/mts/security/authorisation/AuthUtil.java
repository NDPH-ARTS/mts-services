package uk.ac.ox.ndph.mts.security.authorisation;

import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthUtil {

    /**
     * Get all subtrees
     * @param sites list of sites in tree
     * @return HashMap with siteId as key with value list of SiteDTO in it's subtree
     */
    public Map<String, ArrayList<String>> getSiteSubTrees(List<SiteDTO> sites) {

        Map<String, SiteDTO> sitesMap = sites.stream()
                .collect(Collectors.toMap(SiteDTO::getSiteId, Function.identity()));

        //initial subtrees
        HashMap<String, ArrayList<String>> sitesSubTrees  = new HashMap<>();

        for (SiteDTO node : sites) {
            //each subtree should contain the site itself
            if (!sitesSubTrees.containsKey(node.getSiteId())) {
                sitesSubTrees.put(node.getSiteId(), new ArrayList<>(Collections.singletonList(node.getSiteId())));
            }
            //Add the site to all of it's parents
            SiteDTO parentNode = sitesMap.get(node.getParentSiteId());
            while (parentNode != null) {
                addSiteToParentSite(parentNode, node, sitesSubTrees);
                parentNode = sitesMap.get(parentNode.getParentSiteId());
            }
        }

        return sitesSubTrees;
    }

    /**
     * Add site to parent site
     * @param parentSite to add the site too
     * @param siteToAdd the site to add
     * @param sitesSubTrees the subtrees of sites
     */
    private void addSiteToParentSite(SiteDTO parentSite,
                                     SiteDTO siteToAdd,
                                     HashMap<String, ArrayList<String>> sitesSubTrees) {
        if (parentSite.getSiteId() != null) {
            if (!sitesSubTrees.containsKey(parentSite.getSiteId())) {
                sitesSubTrees.put(parentSite.getSiteId(),
                        new ArrayList<>(Collections.singletonList(parentSite.getSiteId())));
            }
            sitesSubTrees.get(parentSite.getSiteId()).add(siteToAdd.getSiteId());
        }
    }

    /**
     * Return all user sites
     * @param sites all trial sites
     * @param roleAssignments user's role assignments
     * @return set of siteIds
     */
    public Set<String> getUserSites(List<SiteDTO> sites, List<RoleAssignmentDTO> roleAssignments) {
        Map<String, ArrayList<String>> tree = getSiteSubTrees(sites);

        return roleAssignments.stream()
                .flatMap(roleAssignmentDTO ->
                        tree.getOrDefault(roleAssignmentDTO.getSiteId(), new ArrayList<>()).stream())
                .collect(Collectors.toSet());
    }

    /**
     * Get site id from an object
     * @param obj object
     * @param methodName object's method name to retrieve sited id
     * @return String site id
     */
    public String getSiteIdFromObj(Object obj, String methodName) {
        try {
            Method getSiteMethod = obj.getClass().getMethod(methodName);
            return Objects.toString(getSiteMethod.invoke(obj), null);
        } catch (Exception e) {
            throw new AuthorisationException("Error parsing sites from request body.", e);
        }
    }
}
