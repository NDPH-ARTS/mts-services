package uk.ac.ox.ndph.mts.practitioner_service.validation;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;

@ExtendWith(MockitoExtension.class)
public class PractitionerUserAccountValidationTests {
    
    private PractitionerUserAccountValidation validator;

    @Mock
    private EntityStore<Practitioner> entityStore;
    
    @BeforeEach
    void setup() {
         this.validator = new PractitionerUserAccountValidation(entityStore);
    }
    
    @Test
    void TestPractitionerUserAccoountValidation_WhenPractitionerHasAccount_FailsValidation() {
        String practitionerId = "practitionerId";
        PractitionerUserAccount userAccount = new PractitionerUserAccount(practitionerId, "userAccountId");
        Practitioner practitioner = new Practitioner("id", "prefix", "given", "family", "anotherAccountId");
               
        when(entityStore.getEntity(practitionerId)).thenReturn(Optional.of(practitioner));
        var result = validator.validate(userAccount);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString("registered"));
    }    
    
    @Test
    void TestPractitionerUserAccoountValidation_WhenPractitionerHasNoAccount_PassesValidation() {
        String practitionerId = "practitionerId";
        PractitionerUserAccount userAccount = new PractitionerUserAccount(practitionerId, "userAccountId");
        Practitioner practitioner = new Practitioner("id", "prefix", "given", "family", "");
               
        when(entityStore.getEntity(practitionerId)).thenReturn(Optional.of(practitioner));
        var result = validator.validate(userAccount);
        assertThat(result.isValid(), is(true));
    }
        
    @Test
    void TestPractitionerUserAccoountValidation_WhenPractitionerDoesNotExist_FailsValidation() {
        String practitionerId = "practitionerId";
        PractitionerUserAccount userAccount = new PractitionerUserAccount(practitionerId, "userAccountId");
               
        when(entityStore.getEntity(practitionerId)).thenReturn(Optional.empty());
        var result = validator.validate(userAccount);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString("Invalid participant id"));
    }

    @ParameterizedTest
    @CsvSource({"practitionerId,,userAccountId",
                ",userAccountId,practitionerId",
                "null,null,practitionerId",
                "null,userAccountId,practitionerId",
                "practitionerId,null,userAccountId"
    })
    void TestPractitionerUserAccountValidation_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
        @ConvertWith(NullableConverter.class) String practitionerId,
        @ConvertWith(NullableConverter.class) String userAccountId,
        String expectedField) {
        // Arrange
        PractitionerUserAccount userAccount = new PractitionerUserAccount(practitionerId, userAccountId);
        
        // Act + Assert
        var result = validator.validate(userAccount);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }    
}
