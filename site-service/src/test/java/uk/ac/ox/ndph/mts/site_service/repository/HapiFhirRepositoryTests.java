package uk.ac.ox.ndph.mts.site_service.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import ca.uhn.fhir.rest.gclient.IQuery;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Test
    void TestHapiRepository_WhenSaveOrganization_SendsBundleWithTransactionType()
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Organization()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);

        var organization = new Organization();

        // Act
        fhirRepository.saveOrganization(organization);

        // Assert
        verify(fhirContextWrapper).executeTrasaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenSaveResearchStudy_SendsBundleWithTransactionType()
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new ResearchStudy()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);

        var researchStudy = new ResearchStudy();

        // Act
        fhirRepository.saveResearchStudy(researchStudy);

        // Assert
        verify(fhirContextWrapper).executeTrasaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }
    
    @Test
    void TestHapiRepository_WhenContextWrapperThrowsExpected_ThrowsRestException(){
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenThrow(new ResourceNotFoundException("error"));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();
        
        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }
    
    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException(){
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Organization(), new Organization()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException(){
        // Arrange
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(null);

        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }

    @Test
    void TestHapiRepository_WhenContextWrapperResearchStudyThrowsExpected_ThrowsRestException(){
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenThrow(new ResourceNotFoundException("error"));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundleResearchStudy_ThrowsRestException(){
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new ResearchStudy(), new ResearchStudy()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNullResearchStudy_ThrowsRestException(){
        // Arrange
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(null);

        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @SuppressWarnings("unchecked")
    private IQuery<Bundle> mockQuery() {
        return mock(IQuery.class);
    }

    @Test
    void TestHapiRepository_getOrganizations_WhenContextReturnsSearch_ReturnsOrganizations() {
        // arrange
        final var org = new Organization()
                .setName("CCO")
                .addAlias("Root");
        org.setId("this-is-my-id");
        final var responseBundle = new Bundle();
        responseBundle.addEntry()
                .setResource(org);
        final var mockQuery = mockQuery();
        when(mockQuery.execute()).thenReturn(responseBundle);
        when(fhirContextWrapper.search(anyString(), eq("Organization"))).thenReturn(mockQuery);
        when(fhirContextWrapper.toListOfResourcesOfType(any(Bundle.class), eq(Organization.class))).thenReturn(List.of(org));
        final var fhirRepository =  new HapiFhirRepository(fhirContextWrapper);
        // act
        final Collection<Organization> orgs = fhirRepository.getOrganizations();
        // assert
        verify(mockQuery).execute();
        assertThat(orgs, is(not(empty())));
        assertThat(orgs.size(), is(1));
        assertThat(orgs, hasItem(hasProperty("name", equalTo("CCO"))));
    }

}
