package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


class OrganizationConverterTest {

    private static final String ORG_ID = "0d24d8bd-b6ba-4742-96fa-68636dafb487";
    private static final String PARENT_ID = "cccccccc-ccc-cccc-cccc-cccccccccccc";
    private static final String NAME = "The Organization";
    private static final String ALIAS = "aka-org";
    private static final String SITE_TYPE = "site-type";

    private final OrganizationConverter orgConverter = new OrganizationConverter();

    @Test
    void TestConvert_AllPropertiesSpecified_returnsMatchingOrg() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, SITE_TYPE);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_IdIsNull_returnsMatchingOrg() {
        // arrange
        final var site = new Site(null, NAME, ALIAS, PARENT_ID, SITE_TYPE);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(),  nullValue());
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_NullParentId_returnsOrgWithNoParent() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, null, SITE_TYPE);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().isEmpty(), equalTo(true));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_EmptyParentId_returnsOrgWithNoParent() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, "", SITE_TYPE);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().isEmpty(), equalTo(true));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_NoAlias_returnsOrgWithoutAlias() {
        // arrange
        final var site = new Site(ORG_ID, NAME, null, PARENT_ID, SITE_TYPE);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().isEmpty(), equalTo(true));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_EmptySiteType_returnsOrgWithNoSiteType() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, "");
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), equalTo(null));
    }
}
