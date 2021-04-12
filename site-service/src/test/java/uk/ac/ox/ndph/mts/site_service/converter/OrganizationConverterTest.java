package uk.ac.ox.ndph.mts.site_service.converter;

import org.hamcrest.core.IsNull;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.site_service.model.SiteAddress;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class OrganizationConverterTest {

    private static final String ORG_ID = "0d24d8bd-b6ba-4742-96fa-68636dafb487";
    private static final String PARENT_ID = "cccccccc-ccc-cccc-cccc-cccccccccccc";
    private static final String NAME = "The Organization";
    private static final String SITE_DESCRIPTION = "Some sort of Organization description";
    private static final String ALIAS = "aka-org";
    private static final String SITE_TYPE = "site-type";
    private static final String ADDRESS1 = "address1";
    private static final String ADDRESS2 = "address2";
    private static final String ADDRESS3 = "address3";
    private static final String ADDRESS4 = "address4";
    private static final String ADDRESS5 = "address5";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String POSTCODE = "postcode";
    private static final SiteAddress SITE_ADDRESS = new SiteAddress(ADDRESS1, ADDRESS2, ADDRESS3, ADDRESS4, ADDRESS5, CITY, COUNTRY, POSTCODE);
    private static final SiteAddress SITE_ADDRESS_WITH_NULLS = new SiteAddress(null, null, null, null, null, null, null, null);

    private final OrganizationConverter orgConverter = new OrganizationConverter();
    private final AddressConverter addressConverter = new AddressConverter();

    @Test
    void TestConvert_AllPropertiesSpecified_returnsMatchingOrg() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, SITE_TYPE, SITE_DESCRIPTION);
        orgConverter.setConverter(addressConverter);
        // act
        final Organization org = orgConverter.convert(site);

        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_AllPropertiesSpecifiedWithAddress_returnsMatchingOrg() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, SITE_TYPE, SITE_DESCRIPTION);
        site.setAddress(SITE_ADDRESS);
        orgConverter.setConverter(addressConverter);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
        assertThat(org.getAddress().get(0).getLine().get(0).getValue(), containsString(ADDRESS1));
        assertThat(org.getAddress().get(0).getLine().get(1).getValue(), containsString(ADDRESS2));
        assertThat(org.getAddress().get(0).getLine().get(2).getValue(), containsString(ADDRESS3));
        assertThat(org.getAddress().get(0).getLine().get(3).getValue(), containsString(ADDRESS4));
        assertThat(org.getAddress().get(0).getLine().get(4).getValue(), containsString(ADDRESS5));
        assertThat(org.getAddress().get(0).getCity(), containsString(CITY));
        assertThat(org.getAddress().get(0).getCountry(), containsString(COUNTRY));
        assertThat(org.getAddress().get(0).getPostalCode(), containsString(POSTCODE));
    }

    @Test
    void TestConvert_AllPropertiesSpecifiedWithNullAddress_returnsMatchingOrg() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, SITE_TYPE, SITE_DESCRIPTION);
        site.setAddress(SITE_ADDRESS_WITH_NULLS);
        orgConverter.setConverter(addressConverter);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
        assertEquals(0, org.getAddress().get(0).getLine().size());
        assertThat(org.getAddress().get(0).getCity(), is(IsNull.nullValue()));
        assertThat(org.getAddress().get(0).getCountry(), is(IsNull.nullValue()));
        assertThat(org.getAddress().get(0).getPostalCode(), is(IsNull.nullValue()));
    }

    @Test
    void TestConvert_IdIsNull_returnsMatchingOrg() {
        // arrange
        final var site = new Site(null, NAME, ALIAS, PARENT_ID, SITE_TYPE, SITE_DESCRIPTION);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
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
        final var site = new Site(ORG_ID, NAME, ALIAS, null, SITE_TYPE, SITE_DESCRIPTION);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
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
        final var site = new Site(ORG_ID, NAME, ALIAS, "", SITE_TYPE, SITE_DESCRIPTION);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
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
        final var site = new Site(ORG_ID, NAME, null, PARENT_ID, SITE_TYPE, SITE_DESCRIPTION);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().isEmpty(), equalTo(true));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), containsString(SITE_TYPE));
    }

    @Test
    void TestConvert_EmptySiteType_returnsOrgWithNoSiteType() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, "", SITE_DESCRIPTION);
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getText().getDiv().allText(), equalTo(site.getDescription()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getImplicitRules(), equalTo(null));
    }

    @Test
    void TestConvert_EmptySiteDescription_returnsOrgWithNoDescriptionType() {
        // arrange
        final var site = new Site(ORG_ID, NAME, ALIAS, PARENT_ID, SITE_TYPE, "");
        // act
        final Organization org = orgConverter.convert(site);
        // assert
        assertThat(org.getName(), equalTo(site.getName()));
        assertThat(org.getIdElement().getIdPart(), equalTo(ORG_ID));
        assertThat(org.getAlias().size(), equalTo(1));
        final String alias = org.getAlias().get(0).getValueAsString();
        assertThat(alias, containsString(ALIAS));
        assertThat(org.getPartOf().getReference(), containsString(PARENT_ID));
        assertThat(org.getText().getDiv().allText(), equalTo(null));
    }
}
