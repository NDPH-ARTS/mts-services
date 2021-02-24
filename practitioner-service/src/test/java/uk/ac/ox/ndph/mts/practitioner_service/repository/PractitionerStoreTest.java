package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import static java.util.Arrays.asList;
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

    private PractitionerStore practitionerStore;

    @BeforeEach
    void setup() {
        practitionerStore = new PractitionerStore(repository, modelToFhirConverter, fhirToModelConverter);
    }

    @Test
    void TestSaveEntity_WhenValidEntity_SaveToRepositoryAndReturnGeneratedId() {
        // arrange
        Practitioner inputPractitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        var outputPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        when(modelToFhirConverter.convert(any(Practitioner.class))).thenReturn(outputPractitioner);
        when(repository.savePractitioner(any(org.hl7.fhir.r4.model.Practitioner.class))).thenReturn("123");

        // act
        var result = practitionerStore.saveEntity(inputPractitioner);

        // assert
        assertThat(result, equalTo("123"));
    }

    @Test
    void TestListEntitiesByUserIdentity_WhenFindByUserIdentity_CallsRepository() {
        // act and assert
        var practitioner = new org.hl7.fhir.r4.model.Practitioner();
        var modelPractitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        when(repository.getPractitionersByUserIdentity("123")).thenReturn(asList(practitioner));
        when(fhirToModelConverter.convertList(asList(practitioner))).thenReturn(asList(modelPractitioner));

        practitionerStore.findEntitiesByUserIdentity("123");
    }
}
