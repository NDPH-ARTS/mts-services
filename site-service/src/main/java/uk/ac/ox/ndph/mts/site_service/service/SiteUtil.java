package uk.ac.ox.ndph.mts.site_service.service;

import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SiteUtil {

    /**
     * Return all parent sites
     * @param siteId for base site
     * @return list of siteIds
     */
    public List<String> getParentSiteIds(String siteId, List<Site> sites) {
        return getSiteAndParents(siteId, convertSiteToDTO(sites));
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

    /**
     * Get all subtrees
     * @param sites list of sites in tree
     * @return HashMap with siteId as key with value list of SiteDTO in it's subtree
     */
    public List<String> getSiteAndParents(String siteId, List<SiteDTO> sites) {

        Map<String, SiteDTO> sitesMap = sites.stream()
            .collect(Collectors.toMap(SiteDTO::getSiteId, Function.identity()));

        List<String> ancestors = new ArrayList<>();
        String searchSiteId = siteId;
        SiteDTO site = sitesMap.get(searchSiteId);

        if (Objects.nonNull(site)) {
            ancestors.add(searchSiteId);
            String parentId = site.getParentSiteId();

            while (parentId != null) {
                ancestors.add(parentId);
                searchSiteId = parentId;
                parentId = sitesMap.get(searchSiteId).getParentSiteId();
            }
        }

        return ancestors;
    }


    public List<SiteDTO> convertSiteToDTO(List<?> sitesReturnObject) {
        return sitesReturnObject.stream()
            .map(siteObject -> {
                var siteId =  getSiteIdFromObj(siteObject, "getSiteId");
                var parentSiteId =  getSiteIdFromObj(siteObject, "getParentSiteId");
                return new SiteDTO(siteId, parentSiteId);
            }).collect(Collectors.toList());
    }

}
