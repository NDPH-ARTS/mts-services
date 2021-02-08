package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.converter.OrganizationConverter;
import uk.ac.ox.ndph.mts.site_service.converter.SiteConverter;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteStoreTests {

    @Mock
    private HapiFhirRepository fhirRepo;

    @Test
    void SiteStoreFindRoot_WhenStoreIsEmpty_ReturnsEmpty() {
        // arrange
        when(fhirRepo.findOrganizationsByPartOf(isNull())).thenReturn(Collections.emptyList());
        final var store = new SiteStore(fhirRepo, new OrganizationConverter(), new SiteConverter());
        // act + assert
        assertThat(store.findRoot().isPresent(), equalTo(false));
    }

    @Test
    void SiteStoreFindRoot_WhenStoreReturnsOrg_ReturnsMatchingSite() {
        // arrange
        final var org = new Organization();
        org.setName("Root");
        when(fhirRepo.findOrganizationsByPartOf(isNull())).thenReturn(Collections.singletonList(org));
        final var store = new SiteStore(fhirRepo, new OrganizationConverter(), new SiteConverter());
        // act + assert
        assertThat(store.findRoot().isPresent(), equalTo(true));
        assertThat(store.findRoot().get().getName(), equalTo(org.getName()));
    }

}
