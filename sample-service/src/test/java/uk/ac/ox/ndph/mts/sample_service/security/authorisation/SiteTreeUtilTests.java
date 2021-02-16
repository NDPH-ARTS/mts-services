package uk.ac.ox.ndph.mts.sample_service.security.authorisation;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.SiteDTO;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteTreeUtilTests {

    private SiteTreeUtil siteTreeUtil;

    @BeforeEach
    void setUp()  {
        this.siteTreeUtil = new SiteTreeUtil();
    }

    @Test
    void TestGetSiteTrees_WithEmptyList_ReturnsEmptyTrees(){

        List<SiteDTO> sites = Lists.emptyList();

        assertTrue(siteTreeUtil.getSiteSubTrees(sites).isEmpty());
    }

    @Test
    void TestGetSiteTrees_WithSingleSite_ReturnsSingleTreeWithItself(){

        SiteDTO expectedSiteDto = getSiteDto("siteId", "parentSiteId");
        List<SiteDTO> sites = Collections.singletonList(expectedSiteDto);

        var actualTree = siteTreeUtil.getSiteSubTrees(sites);
        assertAll(
                ()-> assertEquals(1, actualTree.size()),
                ()-> assertEquals(1, actualTree.get("siteId").size()),
                ()-> assertEquals(expectedSiteDto, actualTree.get("siteId").get(0))
        );
    }

    @Test
    void TestGetSiteTrees_WithChild_ReturnsTreeWithChildren(){

        SiteDTO parentSiteDto = getSiteDto("parentSiteId", null);
        SiteDTO childSiteDto = getSiteDto("childSiteId", "parentSiteId");
        List<SiteDTO> sites = List.of(parentSiteDto, childSiteDto);

        var actualTrees = siteTreeUtil.getSiteSubTrees(sites);

        assertAll(
                //The tree contains all subtrees
                ()-> assertEquals(2, actualTrees.size()),
                // Parent tree contains itself and it's children
                ()-> assertEquals(2, actualTrees.get("parentSiteId").size()),
                ()-> assertTrue(actualTrees.get("parentSiteId").contains(parentSiteDto)),
                ()-> assertTrue(actualTrees.get("parentSiteId").contains(childSiteDto)),
                // The child subtree contains only itself
                ()-> assertEquals(1, actualTrees.get("childSiteId").size()),
                ()-> assertTrue(actualTrees.get("childSiteId").contains(childSiteDto))
        );
    }

    @Test
    void TestGetSiteTrees_WithChildrenAndRandomOrder_ReturnsTreeWithChildrenAsExpectef(){

        SiteDTO parentSiteDto = getSiteDto("parentSiteId", null);
        SiteDTO childSiteDto = getSiteDto("childSiteId", "parentSiteId");
        SiteDTO childSiteDto2 = getSiteDto("childSiteId2", "parentSiteId");

        List<SiteDTO> sites = List.of(childSiteDto, parentSiteDto, childSiteDto2);

        var actualTrees = siteTreeUtil.getSiteSubTrees(sites);

        assertAll(
                //The tree contains all subtrees
                ()-> assertEquals(3, actualTrees.size()),
                // Parent tree contains itself and it's children
                ()-> assertEquals(3, actualTrees.get("parentSiteId").size()),
                ()-> assertTrue(actualTrees.get("parentSiteId").contains(parentSiteDto)),
                ()-> assertTrue(actualTrees.get("parentSiteId").contains(childSiteDto)),
                ()-> assertTrue(actualTrees.get("parentSiteId").contains(childSiteDto2)),
                // The child subtree contains only itself
                ()-> assertEquals(1, actualTrees.get("childSiteId").size()),
                ()-> assertTrue(actualTrees.get("childSiteId").contains(childSiteDto)),
                ()-> assertEquals(1, actualTrees.get("childSiteId2").size()),
                ()-> assertTrue(actualTrees.get("childSiteId2").contains(childSiteDto2))
        );
    }

    private SiteDTO getSiteDto(String siteId, String parentSiteId){
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId(siteId);
        siteDTO.setParentSiteId(parentSiteId);

        return siteDTO;
    }
}
