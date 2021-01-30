package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FhirContextWrapperTest {

    final FhirContextWrapper sut = new FhirContextWrapper();
    final Bundle EMPTY_BUNDLE = new Bundle();
    final Bundle bundle = new Bundle();

    @Test
    void getResourcesFrom_whenNullBundle_thenThrowException() {
        // Act + Assert
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> sut.getResourcesFrom(null, 1))
                .withMessage("Bundle must not be null");
    }

    @Test
    void getResourcesFrom_whenEmptyBundleAndExpectNonZero_thenThrowException() {
        // Act + Assert
        assertThatExceptionOfType(RestException.class)
                .isThrownBy(() -> sut.getResourcesFrom(EMPTY_BUNDLE, 1))
                .withMessage(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), 0));
    }

    @Test
    void getResourcesFrom_whenEmptyBundleAndExpectZero_thenReturnEmptyCollection() {
        // Act
        Collection<IBaseResource> resources = sut.getResourcesFrom(EMPTY_BUNDLE, 0);

        // Assert
        assertThat(resources).isEmpty();
    }

    @Test
    void getResourcesFrom_whenNonZeroResourcesAndExpectDifferentNumber_thenThrowException() {
        // Arrange
        bundle.addEntry().setResource(new Practitioner());
        bundle.addEntry().setResource(new Practitioner());

        // Act + Assert
        assertThatExceptionOfType(RestException.class)
                .isThrownBy(() -> sut.getResourcesFrom(bundle, 1))
                .withMessage(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), 2));
    }

    @Test
    void getResourcesFrom_whenNonZeroResourcesAndExpectSameNumber_thenReturnCorrectList() {
        // Arrange
        final Practitioner PRACTITIONER1 = new Practitioner();
        final Practitioner PRACTITIONER2 = new Practitioner();
        bundle.addEntry().setResource(PRACTITIONER1);
        bundle.addEntry().setResource(PRACTITIONER2);

        // Act
        List<IBaseResource> resources = sut.getResourcesFrom(bundle, 2);

        // Assert
        assertThat(resources).hasSize(2).containsOnlyOnce(PRACTITIONER1).containsOnlyOnce(PRACTITIONER2);
    }
}
