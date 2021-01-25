package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentValidationTests {

    private RoleAssignmentValidation validator;

    private static final String ROLE_SERVICE_URLBASE = "http://localhost:82";

    private WebClient webClient() {
        return WebClient.create();
    }

    @BeforeEach void init() {
        this.validator = new RoleAssignmentValidation(webClient(), ROLE_SERVICE_URLBASE);
    }

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
        // Act + Assert
        var result = validator.validate(roleAssignment);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @Test
    void TestRoleAssignmentValidation_WhenValidPractitioner_ReturnsValidResponse() {
        // Arrange
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", "roleId");
        // Act
        var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
    }

}
