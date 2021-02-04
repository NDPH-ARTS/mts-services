package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SiteConverterTest {
    @Test
    void TestConvert_AllPropertiesSpecified_returnsMatchingSite() throws Exception {
        // arrange
        final String alias = "the-alias";
        final String parentId = "the-parent-id";
        final Organization org = new Organization();
        org.setName("the-name");
        org.setId("the-id");
        org.addAlias(alias);
        org.setPartOf(new Reference("Organization/" + parentId));
        // act
        final Site site = new SiteConverter().convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(site.getSiteId(), is(equalTo(org.getId())));
        assertThat(site.getAlias(), is(equalTo(alias)));
        assertThat(site.getParentSiteId(), is(equalTo(parentId)));
    }

    @Test
    void TestConvert_NotPartof_returnsSiteWithNoParent() throws Exception {
        // arrange
        final String alias = "the-alias";
        final Organization org = new Organization();
        org.setName("the-name");
        org.setId("the-id");
        org.addAlias(alias);
        // act
        final Site site = new SiteConverter().convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(site.getSiteId(), is(equalTo(org.getId())));
        assertThat(site.getAlias(), is(equalTo(alias)));
        assertThat(site.getParentSiteId(), is(nullValue()));
    }

    @Test
    void TestConvert_NoAlias_returnsSiteWithoutAlias() throws Exception {
        // arrange
        final String parentId = "the-parent-id";
        final Organization org = new Organization();
        org.setName("the-name");
        org.setId("the-id");
        org.setPartOf(new Reference("Organization/" + parentId));
        // act
        final Site site = new SiteConverter().convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(site.getSiteId(), is(equalTo(org.getId())));
        assertThat(site.getAlias(), is(nullValue()));
        assertThat(site.getParentSiteId(), is(equalTo(parentId)));
    }

    @Test
    void TestConvert_NullAlias_returnsSiteWithoutAlias() throws Exception {
        // arrange
        final String parentId = "the-parent-id";
        final Organization org = new Organization();
        org.setName("the-name");
        org.setId("the-id");
        org.setPartOf(new Reference("Organization/" + parentId));
        org.addAlias(null);
        // act
        final Site site = new SiteConverter().convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(site.getSiteId(), is(equalTo(org.getId())));
        assertThat(site.getAlias(), is(nullValue()));
        assertThat(site.getParentSiteId(), is(equalTo(parentId)));
    }

}
