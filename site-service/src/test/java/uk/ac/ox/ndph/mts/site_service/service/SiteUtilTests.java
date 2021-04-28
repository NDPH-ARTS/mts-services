package uk.ac.ox.ndph.mts.site_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SiteUtilTests {

    private SiteUtil siteUtil;

    @BeforeEach
    void setUp()  {
        this.siteUtil = new SiteUtil();
    }

    @Test
    void TestGetSiteIdFromObj_ForObjectWithSite_ReturnsSiteId(){
        //Arrange
        var objectWithSite = getSiteDto("siteId", "parentSiteId");

        //Act
        //Assert
        assertEquals(siteUtil.getSiteIdFromObj(objectWithSite, "getSiteId"), "siteId");

    }

    @Test
    void TestGetSiteIdFromObj_WithInvalidMethodName_ReturnsSiteId(){
        //Arrange
        var objectWithSite = getSiteDto("siteId", "parentSiteId");

        //Act
        //Assert
        assertThrows(AuthorisationException.class, () -> siteUtil.getSiteIdFromObj(objectWithSite, "invalidMethodName"));

    }

    @Test
    void TestGetParentSiteIdsReturnParentsFromChild(){
        //Arrange
        Site parentSite = getSite("parentSiteId", null);
        Site childSite = getSite("childSiteId", "parentSiteId");
        Site childSite2 = getSite("childSiteId2", "childSiteId");

        List<Site> sites = List.of(childSite, parentSite, childSite2);

        //Act
        List<String> allSites = siteUtil.getParentSiteIds("childSiteId2", sites);
        List<String> twoSites = siteUtil.getParentSiteIds("childSiteId", sites);
        List<String> oneSite = siteUtil.getParentSiteIds("parentSiteId", sites);
        List<String> zeroSites = siteUtil.getParentSiteIds("xxxxxx", sites);

        //Assert
        assertAll(
            //The tree contains all subtrees
            ()-> assertEquals(3, allSites.size()),
            // Parent tree contains itself and it's children
            ()-> assertEquals(2, twoSites.size()),
            ()-> assertEquals(1, oneSite.size()),
            ()-> assertEquals(0, zeroSites.size()));
    }

    private Site getSite(String siteId, String parentSiteId) {
        Site site = new Site();
        site.setSiteId(siteId);
        site.setParentSiteId(parentSiteId);

        return site;

    }


    private SiteDTO getSiteDto(String siteId, String parentSiteId){
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId(siteId);
        siteDTO.setParentSiteId(parentSiteId);

        return siteDTO;
    }
}
