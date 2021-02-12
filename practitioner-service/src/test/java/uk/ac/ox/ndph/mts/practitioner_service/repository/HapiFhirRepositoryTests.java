package uk.ac.ox.ndph.mts.practitioner_service.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.gclient.ICriterion;
import com.jayway.jsonpath.Criteria;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import uk.ac.ox.ndph.mts.practitioner_service.converter.PractitionerRoleConverter;
import ca.uhn.fhir.rest.server.exceptions.UnclassifiedServerFailureException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import javax.naming.Name;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    private FhirRepository repository;

    @BeforeEach
    void init() {
        this.repository = new HapiFhirRepository(fhirContextWrapper);
    }

    @Test
    void TestGetEntity_When_IdValid() {
        String id = "42";
		Practitioner practitioner = new Practitioner();
        when(fhirContextWrapper.getById(id)).thenReturn(practitioner);
        assertEquals(practitioner, repository.getPractitioner(id).get());
    }

    @Test
    void TestGetEntity_When_FhirException() {
        String id = "22";
        when(fhirContextWrapper.getById(id)).thenThrow(new UnclassifiedServerFailureException(500, "Error"));
        assertThrows(RestException.class, () -> repository.getPractitioner(id));
    }

    @Test
    void TestHapiRepository_WhenSavePractitioner_SendsBundleWithTransactionType() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var practitioner = new Practitioner();

        // Act
        repository.savePractitioner(practitioner);

        // Assert
        verify(fhirContextWrapper).executeTransaction(bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenContextWrapperThrowsException_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenThrow(exception);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitioner(practitioner));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitioner(practitioner));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(null);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitioner(practitioner));
    }


    // PractitionerRole Tests

    @Test
    void TestSavePractitionerRole_WhenSaveEntity_SendsBundleWithTransactionType() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new PractitionerRole()));
        var entity = new PractitionerRole();

        // Act
        repository.savePractitionerRole(entity);

        // Assert
        verify(fhirContextWrapper).executeTransaction(bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestSavePractitionerRole_WhenSaveEntity_ReturnsCorrectId() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        var entity = new PractitionerRole();
        entity.setId("123");
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(entity));

        // Act
        var value = repository.savePractitionerRole(entity);

        // Assert
        assertThat(value, equalTo("123"));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionThrowsException_ThrowsRestException() throws FhirServerResponseException {
        FhirServerResponseException DUMMY_EXCEPTION = new FhirServerResponseException("", null);
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenThrow(DUMMY_EXCEPTION);
        var entity = new PractitionerRole();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionReturnsEmptyBundle_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var entity = new PractitionerRole();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionReturnsNull_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(null);
        var entity = new PractitionerRole();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestGetPractitionerRolesByUserIdentity_WhenSearchResource_ReturnsEmptyList(){
        // Arrange
        when(fhirContextWrapper.searchResource(any(), any(ICriterion.class))).thenReturn(new ArrayList<>());
        var value = repository.getPractitionerRolesByUserIdentity("123");

        assertThat(value, equalTo(new ArrayList<PractitionerRole>()));
    }

    @Test
    void TestGetPractitionerRolesByUserIdentity_WhenSearchResource_ReturnsNotEmptyList(){
        // Arrange
        var response = new ArrayList<IBaseResource>();

        org.hl7.fhir.r4.model.PractitionerRole fhirPractitionerRole = new org.hl7.fhir.r4.model.PractitionerRole();
        fhirPractitionerRole.setOrganization(new Reference("Organization/site123"));
        fhirPractitionerRole.setPractitioner(new Reference("Practitioner/123"));
        fhirPractitionerRole.addCode().setText("role123");
        response.add(fhirPractitionerRole);

        when(fhirContextWrapper.searchResource(any(), any(ICriterion.class))).thenReturn(response);
        var value = repository.getPractitionerRolesByUserIdentity("123");

        PractitionerRoleConverter practitionerRoleConverter = new PractitionerRoleConverter();
        RoleAssignment roleAssignment = practitionerRoleConverter.convert(value.get(0));

        assertAll(
                () ->assertThat(value.size(), equalTo(1)),
                () ->assertThat(roleAssignment.getPractitionerId(), equalTo("123") ),
                () ->assertThat(roleAssignment.getSiteId(), equalTo("site123")),
                () ->assertThat(roleAssignment.getRoleId(), equalTo("role123"))
                );
    }
}
