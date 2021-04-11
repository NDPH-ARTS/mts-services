package uk.ac.ox.ndph.mts.security.authorisation;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
