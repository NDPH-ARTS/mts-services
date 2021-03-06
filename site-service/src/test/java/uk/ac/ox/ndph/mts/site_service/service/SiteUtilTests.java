package uk.ac.ox.ndph.mts.site_service.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.site_service.model.SiteDTO;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteUtilTests {

    private SiteUtil siteUtil;

    @BeforeEach
    void setUp()  {
        this.siteUtil = new SiteUtil();
    }

    @Test
    void TestGetSiteTrees_WithEmptyList_ReturnsEmptyTrees(){

        //Arrange
        List<SiteDTO> sites = Lists.emptyList();

        //Act
        //Assert
        assertTrue(siteUtil.getSiteSubTrees(sites).isEmpty());
    }

    @Test
    void TestGetSiteTrees_WithSingleSite_ReturnsSingleTreeWithItself(){

        //Arrange
        SiteDTO expectedSiteDto = getSiteDto("siteId", "parentSiteId");
        List<SiteDTO> sites = Collections.singletonList(expectedSiteDto);

        //Act
        var actualTree = siteUtil.getSiteSubTrees(sites);

        //Assert
        assertAll(
            ()-> assertEquals(1, actualTree.size()),
            ()-> assertEquals(1, actualTree.get("siteId").size()),
            ()-> assertEquals(expectedSiteDto.getSiteId(), actualTree.get("siteId").get(0))
        );
    }

    @Test
    void TestGetSiteTrees_WithChild_ReturnsTreeWithChildren(){
        //Arrange
        SiteDTO parentSiteDto = getSiteDto("parentSiteId", null);
        SiteDTO childSiteDto = getSiteDto("childSiteId", "parentSiteId");
        List<SiteDTO> sites = List.of(parentSiteDto, childSiteDto);

        //Act
        var actualTrees = siteUtil.getSiteSubTrees(sites);

        //Assert
        assertAll(
            //The tree contains all subtrees
            ()-> assertEquals(2, actualTrees.size()),
            // Parent tree contains itself and it's children
            ()-> assertEquals(2, actualTrees.get("parentSiteId").size()),
            ()-> assertTrue(actualTrees.get("parentSiteId").contains(parentSiteDto.getSiteId())),
            ()-> assertTrue(actualTrees.get("parentSiteId").contains(childSiteDto.getSiteId())),
            // The child subtree contains only itself
            ()-> assertEquals(1, actualTrees.get("childSiteId").size()),
            ()-> assertTrue(actualTrees.get("childSiteId").contains(childSiteDto.getSiteId()))
        );
    }

    @Test
    void TestGetSiteTrees_WithChildrenAndRandomOrder_ReturnsTreeWithChildrenAsExpectef(){
        //Arrange
        SiteDTO parentSiteDto = getSiteDto("parentSiteId", null);
        SiteDTO childSiteDto = getSiteDto("childSiteId", "parentSiteId");
        SiteDTO childSiteDto2 = getSiteDto("childSiteId2", "parentSiteId");

        List<SiteDTO> sites = List.of(childSiteDto, parentSiteDto, childSiteDto2);

        //Act
        var actualTrees = siteUtil.getSiteSubTrees(sites);

        //Assert
        assertAll(
            //The tree contains all subtrees
            ()-> assertEquals(3, actualTrees.size()),
            // Parent tree contains itself and it's children
            ()-> assertEquals(3, actualTrees.get("parentSiteId").size()),
            ()-> assertTrue(actualTrees.get("parentSiteId").contains(parentSiteDto.getSiteId())),
            ()-> assertTrue(actualTrees.get("parentSiteId").contains(childSiteDto.getSiteId())),
            ()-> assertTrue(actualTrees.get("parentSiteId").contains(childSiteDto2.getSiteId())),
            // The child subtree contains only itself
            ()-> assertEquals(1, actualTrees.get("childSiteId").size()),
            ()-> assertTrue(actualTrees.get("childSiteId").contains(childSiteDto.getSiteId())),
            ()-> assertEquals(1, actualTrees.get("childSiteId2").size()),
            ()-> assertTrue(actualTrees.get("childSiteId2").contains(childSiteDto2.getSiteId()))
        );
    }

    @Test
    void TestGetUserSites_WithRootRoleAssignment_ReturnsAllSites(){
        //Arrange
        var parentSite = getSiteDto("cco", null);
        var childSite1 = getSiteDto("regiona", parentSite.getSiteId());
        var childSite2 = getSiteDto("regionb", parentSite.getSiteId());

        var sites = List.of(parentSite, childSite1, childSite2);

        var roleAssignment= getRoleAssignment("roleId", parentSite.getSiteId());

        //Act
        var actualResult = siteUtil.getUserSites(sites, Collections.singletonList(roleAssignment));

        //Assert
        assertAll(
            //The tree contains all subtrees
            ()-> assertEquals(3, actualResult.size()),
            ()-> assertTrue(actualResult.contains(parentSite.getSiteId())),
            ()-> assertTrue(actualResult.contains(childSite1.getSiteId())),
            ()-> assertTrue(actualResult.contains(childSite2.getSiteId()))
        );

    }

    @Test
    void TestGetUserSites_WithRoleAssignment_ReturnsSitesAsExpected(){
        //Arrange
        var parentSite = getSiteDto("cco", null);
        var childSite1 = getSiteDto("regiona", parentSite.getSiteId());
        var childSite2 = getSiteDto("regionb", parentSite.getSiteId());

        var sites = List.of(parentSite, childSite1, childSite2);

        var roleAssignment= getRoleAssignment("roleId", childSite1.getSiteId());

        //Act
        var actualResult = siteUtil.getUserSites(sites, Collections.singletonList(roleAssignment));

        //Assert
        assertAll(
            //The tree contains all subtrees
            ()-> assertEquals(1, actualResult.size()),
            ()-> assertTrue(actualResult.contains(childSite1.getSiteId()))
        );

    }


    @Test
    void TestGetParentSiteIdsReturnParentsFromChild(){
        //Arrange
        SiteDTO parentSite = getSiteDto("parentSiteId", null);
        SiteDTO childSite = getSiteDto("childSiteId", "parentSiteId");
        SiteDTO childSite2 = getSiteDto("childSiteId2", "childSiteId");

        List<SiteDTO> sites = List.of(childSite, parentSite, childSite2);

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

    private SiteDTO getSiteDto(String siteId, String parentSiteId){
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId(siteId);
        siteDTO.setParentSiteId(parentSiteId);

        return siteDTO;
    }

    private RoleAssignmentDTO getRoleAssignment(String roleId, String siteId){
        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO();
        roleAssignmentDTO.setSiteId(siteId);
        roleAssignmentDTO.setRoleId(roleId);

        return roleAssignmentDTO;
    }
}
