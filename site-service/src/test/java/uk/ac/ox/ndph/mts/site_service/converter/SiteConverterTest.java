package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


class SiteConverterTest {

    private static final String SERVER_ORG_ID = "http://fhir-api/Organization/0d24d8bd-b6ba-4742-96fa-68636dafb487";
    private static final String SERVER_PARENT_ID = "http://fhir-api/Organization/cccccccc-ccc-cccc-cccc-cccccccccccc";
    private static final String ORG_NAME = "The Organization";
    private static final String ORG_ALIAS = "aka-org";
    private static final String ADDRESS_1 = "address1";
    private static final String ADDRESS_2 = "address2";
    private static final String ADDRESS_3 = "address3";
    private static final String ADDRESS_4 = "address4";
    private static final String ADDRESS_5 = "address5";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String POSTCODE = "postcode";

    private final SiteConverter siteConverter = new SiteConverter();

    @Test
    void TestConvert_AllPropertiesSpecified_returnsMatchingSite() {
        // arrange
        final Organization org = new Organization();
        org.setName(ORG_NAME);
        org.setId(SERVER_ORG_ID);
        org.addAlias(ORG_ALIAS);
        org.setPartOf(new Reference(SERVER_PARENT_ID));
        // act
        final Site site = siteConverter.convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(SERVER_ORG_ID, containsString(site.getSiteId()));
        assertThat(site.getAlias(), is(equalTo(ORG_ALIAS)));
        assertThat(SERVER_PARENT_ID, containsString(site.getParentSiteId()));
    }

    @Test
    void TestConvert_AllPropertiesIncludingAddressSpecified_returnsMatchingSite() {
        // arrange
        final Organization org = new Organization();
        org.setName(ORG_NAME);
        org.setId(SERVER_ORG_ID);
        org.addAlias(ORG_ALIAS);
        org.setPartOf(new Reference(SERVER_PARENT_ID));
        org.addAddress().addLine(ADDRESS_1).addLine(ADDRESS_2).
                addLine(ADDRESS_3).addLine(ADDRESS_4).addLine(ADDRESS_5).
                setCity(CITY).setCountry(COUNTRY).setPostalCode(POSTCODE);
        // act
        final Site site = siteConverter.convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(SERVER_ORG_ID, containsString(site.getSiteId()));
        assertThat(site.getAlias(), is(equalTo(ORG_ALIAS)));
        assertThat(SERVER_PARENT_ID, containsString(site.getParentSiteId()));
        assertThat(site.getAddress().getAddress1(), is(equalTo(ADDRESS_1)));
        assertThat(site.getAddress().getAddress2(), is(equalTo(ADDRESS_2)));
        assertThat(site.getAddress().getAddress3(), is(equalTo(ADDRESS_3)));
        assertThat(site.getAddress().getAddress4(), is(equalTo(ADDRESS_4)));
        assertThat(site.getAddress().getAddress5(), is(equalTo(ADDRESS_5)));
        assertThat(site.getAddress().getCity(), is(equalTo(CITY)));
        assertThat(site.getAddress().getCountry(), is(equalTo(COUNTRY)));
        assertThat(site.getAddress().getPostcode(), is(equalTo(POSTCODE)));
    }

    @Test
    void TestConvert_AllPropertiesIncludingEmptyAddressSpecified_returnsMatchingSite() {
        // arrange
        final Organization org = new Organization();
        org.setName(ORG_NAME);
        org.setId(SERVER_ORG_ID);
        org.addAlias(ORG_ALIAS);
        org.setPartOf(new Reference(SERVER_PARENT_ID));
        org.addAddress().setId("1");
        // act
        final Site site = siteConverter.convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(SERVER_ORG_ID, containsString(site.getSiteId()));
        assertThat(site.getAlias(), is(equalTo(ORG_ALIAS)));
        assertThat(SERVER_PARENT_ID, containsString(site.getParentSiteId()));
        assertThat(site.getAddress().getAddress1(), is(equalTo("")));
        assertThat(site.getAddress().getAddress2(), is(equalTo("")));
        assertThat(site.getAddress().getAddress3(), is(equalTo("")));
        assertThat(site.getAddress().getAddress4(), is(equalTo("")));
        assertThat(site.getAddress().getAddress5(), is(equalTo("")));
        assertThat(site.getAddress().getCity(), is(equalTo("")));
        assertThat(site.getAddress().getCountry(), is(equalTo("")));
        assertThat(site.getAddress().getPostcode(), is(equalTo("")));
    }

    @Test
    void TestConvert_NotPartof_returnsSiteWithNoParent() {
        // arrange
        final Organization org = new Organization();
        org.setName(ORG_NAME);
        org.setId(SERVER_ORG_ID);
        org.addAlias(ORG_ALIAS);
        // act
        final Site site = siteConverter.convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(SERVER_ORG_ID, containsString(site.getSiteId()));
        assertThat(site.getAlias(), is(equalTo(ORG_ALIAS)));
        assertThat(site.getParentSiteId(), is(nullValue()));
    }

    @Test
    void TestConvert_NoAlias_returnsSiteWithoutAlias() {
        // arrange
        final Organization org = new Organization();
        org.setName(ORG_NAME);
        org.setId(SERVER_ORG_ID);
        org.setPartOf(new Reference(SERVER_PARENT_ID));
        // act
        final Site site = siteConverter.convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(SERVER_ORG_ID, containsString(site.getSiteId()));
        assertThat(site.getAlias(), is(nullValue()));
        assertThat(SERVER_PARENT_ID, containsString(site.getParentSiteId()));
    }

    @Test
    void TestConvert_NullAlias_returnsSiteWithoutAlias() {
        // arrange
        final Organization org = new Organization();
        org.setName(ORG_NAME);
        org.setId(SERVER_ORG_ID);
        org.setPartOf(new Reference(SERVER_PARENT_ID));
        org.addAlias(null);
        // act
        final Site site = siteConverter.convert(org);
        // assert
        assertThat(site.getName(), is(equalTo(org.getName())));
        assertThat(SERVER_ORG_ID, containsString(site.getSiteId()));
        assertThat(site.getAlias(), is(nullValue()));
        assertThat(SERVER_PARENT_ID, containsString(site.getParentSiteId()));
    }

}
