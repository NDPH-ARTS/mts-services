package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.assertj.core.api.Assertions;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FhirContextWrapperTest {

    @Test
    void getResourcesFrom_whenEmptyBundleAndExpectNonZero_thenThrowException() {
        // Arrange
        FhirContextWrapper sut = new FhirContextWrapper();
        Bundle bundleNoResource = new Bundle();

        // Act + Assert
        Assertions.assertThatExceptionOfType(RestException.class)
                .isThrownBy(() -> sut.getResourcesFrom(bundleNoResource, 1))
                .withMessage(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), 0));
    }

    @Test
    void getResourcesFrom_whenEmptyBundleAndExpectZero_thenReturnEmptyCollection() {
        FhirContextWrapper sut = new FhirContextWrapper();
        Bundle bundleMultiple = new Bundle();

        // Act
        Collection<IBaseResource> resources = sut.getResourcesFrom(bundleMultiple, 0);

        // Assert
        assertThat(resources).isEmpty();
    }

    @Test
    void getResourcesFrom_whenNonZeroResourcesAndExpectDifferentNumber_thenThrowException() {
        FhirContextWrapper sut = new FhirContextWrapper();
        Bundle bundleMultiple = new Bundle();
        bundleMultiple.addEntry().setResource(new Practitioner());
        bundleMultiple.addEntry().setResource(new Practitioner());

        // Act + Assert
        Assertions.assertThatExceptionOfType(RestException.class)
                .isThrownBy(() -> sut.getResourcesFrom(bundleMultiple, 1))
                .withMessage(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), 2));
    }

    @Test
    void getResourcesFrom_whenNonZeroResourcesAndExpectSameNumber_thenReturnCorrectList() {
        FhirContextWrapper sut = new FhirContextWrapper();
        Bundle bundleMultiple = new Bundle();
        final Practitioner PRACTITIONER1 = new Practitioner();
        final Practitioner PRACTITIONER2 = new Practitioner();
        bundleMultiple.addEntry().setResource(PRACTITIONER1);
        bundleMultiple.addEntry().setResource(PRACTITIONER2);

        // Act
        List<IBaseResource> resources = sut.getResourcesFrom(bundleMultiple, 2);

        // Assert
        assertThat(resources).hasSize(2).containsOnlyOnce(PRACTITIONER1).containsOnlyOnce(PRACTITIONER2);
    }
}
