package uk.ac.ox.ndph.mts.init_service.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.ox.ndph.mts.init_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;

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
        practitionerStore = new PractitionerStore(repository, modelToFhirConverter);
    }

    @Test
    void testSave_WhenValid_SaveToRepositoryAndReturnGeneratedId() {
        Practitioner inputPractitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        var outputPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        when(modelToFhirConverter.convert(any(Practitioner.class))).thenReturn(outputPractitioner);
        when(repository.savePractitioner(any(org.hl7.fhir.r4.model.Practitioner.class))).thenReturn("123");

        var result = practitionerStore.save(inputPractitioner);

        assertThat(result, equalTo("123"));
    }
    

    void TestSaveRoleAssignment_WhenValid_SaveToRepositoryAndReturnGeneratedId() {
        when(repository.savePractitionerRole(any(org.hl7.fhir.r4.model.PractitionerRole.class))).thenReturn("123");

        var result = practitionerStore.savePractitionerRole(new PractitionerRole());

        //assert
        assertThat(result, equalTo("123"));
    }
}
