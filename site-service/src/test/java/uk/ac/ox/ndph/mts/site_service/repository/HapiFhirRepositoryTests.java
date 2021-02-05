package uk.ac.ox.ndph.mts.site_service.repository;

import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Test
    void TestHapiRepository_WhenSaveOrganization_SendsBundleWithTransactionType() throws FhirServerResponseException
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Organization()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);

        var organization = new Organization();

        // Act
        fhirRepository.saveOrganization(organization);

        // Assert
        verify(fhirContextWrapper).executeTransaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenSearchOrganizationByName_Success() throws FhirServerResponseException
    {
        // Arrange
        var organization = new Organization();
        organization.setName("abc");
        var responseBundle = new Bundle();
        responseBundle.addEntry();

        when(fhirContextWrapper.executeSearchByName(anyString(), anyString())).thenReturn(responseBundle);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var uri = "";
        var name = "findByName";

        // Act
        Organization org = fhirRepository.findOrganizationByName(name);

        // Assert
        verify(fhirContextWrapper).executeSearchByName(uri, name);
        assertEquals(null, org);
    }

    @Test
    void TestHapiRepository_WhenSearchOrganizationByName_Sends_NULL() throws FhirServerResponseException
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeSearchByName(anyString(), anyString())).thenReturn(responseBundle);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var uri = "";
        var name = "findByName";

        // Act
        Organization org = fhirRepository.findOrganizationByName(name);

        // Assert
        verify(fhirContextWrapper).executeSearchByName(uri, name);
        assertEquals(null, org);
    }

    @Test
    void TestHapiRepository_WhenSearchOrganizationByName_ThrowsException() throws FhirServerResponseException {

        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeSearchByName(anyString(), anyString())).thenThrow(exception);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        String organizationName = "abc";

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.findOrganizationByName(organizationName));

    }

    @Test
    void TestHapiRepository_WhenSaveResearchStudy_SendsBundleWithTransactionType() throws FhirServerResponseException
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new ResearchStudy()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);

        var researchStudy = new ResearchStudy();

        // Act
        fhirRepository.saveResearchStudy(researchStudy);

        // Assert
        verify(fhirContextWrapper).executeTransaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }
    
    @Test
    void TestHapiRepository_WhenContextWrapperThrowsExpected_ThrowsException() throws FhirServerResponseException
    {
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenThrow(exception);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();
        
        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }
    
    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException()  throws FhirServerResponseException
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Organization(), new Organization()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException() throws FhirServerResponseException
    {
        // Arrange
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(null);

        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }

    @Test
    void TestHapiRepository_WhenContextWrapperResearchStudyThrowsExpected_ThrowsRestException()  throws FhirServerResponseException
    {
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenThrow(exception);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundleResearchStudy_ThrowsRestException()  throws FhirServerResponseException
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new ResearchStudy(), new ResearchStudy()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNullResearchStudy_ThrowsRestException() throws FhirServerResponseException
    {
        // Arrange
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(null);

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
        when(fhirContextWrapper.search(anyString(), eq(Organization.class))).thenReturn(mockQuery);
        when(fhirContextWrapper.toListOfResourcesOfType(any(Bundle.class), eq(Organization.class))).thenReturn(List.of(org));
        final var fhirRepository =  new HapiFhirRepository(fhirContextWrapper);
        // act
        final Collection<Organization> orgs = fhirRepository.findOrganizations();
        // assert
        verify(mockQuery).execute();
        assertThat(orgs, is(not(empty())));
        assertThat(orgs.size(), is(1));
        assertThat(orgs, hasItem(hasProperty("name", equalTo("CCO"))));
    }

    @Test
    void TestHapiRepository_getOrganizations_WhenContextWrapperThrowsExpected_ThrowsRestException() {
        when(fhirContextWrapper.search(anyString(), eq(Organization.class))).thenThrow(new ResourceNotFoundException("error"));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        // Act + Assert
        assertThrows(RestException.class, fhirRepository::findOrganizations);
    }

}
