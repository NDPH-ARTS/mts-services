package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.converter.FhirPractitionerConverter;
import uk.ac.ox.ndph.mts.practitioner_service.converter.ModelPractitionerConverter;
import uk.ac.ox.ndph.mts.practitioner_service.converter.PractitionerRoleConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PractitionerStoreTest {

    @Mock
    private FhirRepository repository;

    @Mock
    private EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> modelToFhirConverter;
    @Mock
    private EntityConverter<org.hl7.fhir.r4.model.Practitioner, Practitioner> fhirToModelConverter;

    @Test
    void TestSaveEntity_WhenValidEntity_SaveToRepositoryAndReturnGeneratedId() {
        //arrange
        Practitioner inputPractitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        var outputPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        when(modelToFhirConverter.convert(any(Practitioner.class))).thenReturn(outputPractitioner);
        when(repository.savePractitioner(any(org.hl7.fhir.r4.model.Practitioner.class))).thenReturn("123");

        //act
        PractitionerStore practitionerStore = new PractitionerStore(repository, modelToFhirConverter, fhirToModelConverter);
        var result = practitionerStore.saveEntity(inputPractitioner);

        //assert
        assertThat(result, equalTo("123"));
    }

    @Test
    void TestListPractitionersByUserIdentity_WhenListByUserIdentity_ReturnsListOfPractitioners() {

        PractitionerStore practitionerStore = new PractitionerStore(repository, modelToFhirConverter, fhirToModelConverter);

        List<org.hl7.fhir.r4.model.Practitioner> fhirPractitionerList =
                Collections.singletonList(new org.hl7.fhir.r4.model.Practitioner());
        List<Practitioner> ourPractitionerList =
                Collections.singletonList(
                new Practitioner("some-id", "some-prefix", "some-name", "some-family-name", "some-id"));
        when(repository.getPractitionersByUserIdentity(any(String.class))).thenReturn(fhirPractitionerList);
        when(fhirToModelConverter.convertList(fhirPractitionerList)).thenReturn(ourPractitionerList);

        List<uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner> convertedPractitionersList = practitionerStore.findEntitiesByUserIdentity("123");

        assertEquals(1, convertedPractitionersList.size());
    }
}
