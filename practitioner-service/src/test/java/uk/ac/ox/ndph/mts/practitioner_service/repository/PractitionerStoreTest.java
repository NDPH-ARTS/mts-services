package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
        var inputPractitioner = new Practitioner(null, "prefix", "givenName", "familyName");
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
    void TestListEntitiesByUserIdentity_WhenListByUserIdentity_ThrowsNotImplementedException() {
        //act and assert
        PractitionerStore practitionerStore = new PractitionerStore(repository, modelToFhirConverter, fhirToModelConverter);
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> practitionerStore.findEntitiesByUserIdentity("123"),
                "Expecting to throw an exception");
    }
}
