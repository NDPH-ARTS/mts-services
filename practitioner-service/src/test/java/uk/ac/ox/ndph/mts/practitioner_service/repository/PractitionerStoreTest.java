package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.practitioner_service.converter.PractitionerConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PractitionerStoreTest {

    @Test
    void whenCreatingEntity_shouldGenerateAndPassOnNewPersonId() {
        // Arrange
        FhirRepository fhirRepo = mock(FhirRepository.class);
        PractitionerConverter converter = new PractitionerConverter();
        IdGenerator idGenerator = mock(IdGenerator.class);
        PractitionerStore sut = new PractitionerStore(fhirRepo, converter, idGenerator);
        Practitioner staff = mock(Practitioner.class); //new Practitioner("", "", "");
        when(idGenerator.generateId()).thenReturn("678");

        // Act
        sut.createEntity(staff);

        // TODO verify any non-blank string is passed in as person ID
        // TODO a collaborator could generate the person ID
        verify(fhirRepo).createPractitioner(any(org.hl7.fhir.r4.model.Practitioner.class), "678");
        //verify(idGenerator, times(1)).generateId();
    }
}
