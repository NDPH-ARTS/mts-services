package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentValidationTests {

    @ParameterizedTest
    @CsvSource({",,,practitionerId",
            ",siteId,roleId,practitionerId",
            "null,siteId,roleId,practitionerId",
            "practitionerId,,roleId,siteId",
            "practitionerId,null,roleId,siteId",
            "practitionerId,siteId,,roleId",
            "practitionerId,siteId,null,roleId",
    })
    void TestRoleAssignmentValidation_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String practitionerId, @ConvertWith(NullableConverter.class) String siteId,
            @ConvertWith(NullableConverter.class) String roleId, String expectedField) {
        // Arrange
        RoleAssignment roleAssignment = new RoleAssignment(practitionerId, siteId, roleId);
        var validator = new RoleAssignmentValidation();

        // Act + Assert
        var result = validator.validate(roleAssignment);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @Test
    void TestRoleAssignmentValidation_WhenValidPractitioner_ReturnsValidResponse() {
        // Arrange
        RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", "roleId");
        var validator = new RoleAssignmentValidation();

        // Act
        var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
    }

}
