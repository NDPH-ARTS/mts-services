package uk.ac.ox.ndph.mts.practitioner_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerAttribute;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ServerError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;

/**
 * Implement an EntityService interface.
 * Validation of practitioner based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class PractitionerService implements EntityService {
    private static final String REGEX_ALL = ".*";
    private static final String FIELD_NAME_PREFIX = "prefix";
    private static final String FIELD_NAME_GIVEN_NAME = "givenName";
    private static final String FIELD_NAME_FAMILY_NAME = "familyName";
    private static final String ERROR_MESSAGE = "value of argument %s cannot be empty";
    private static final String LOG_START = "Loaded practitioner service with configuration: %s";

    private final FhirRepository fhirRepository;    
    private final Map<String, Pair<String, Pattern>> validationMap;
    private final Logger logger = LoggerFactory.getLogger(PractitionerService.class);

    /**
     *
     * @param fhirRepository - FHIR repository interface
     * @param configurationProvider - provider of validation configuration
     */
    @Autowired
    public PractitionerService(FhirRepository fhirRepository,
        PractitionerConfigurationProvider configurationProvider) {
        this.fhirRepository = fhirRepository;
        validationMap = new HashMap<>();
        var configuration = configurationProvider.getConfiguration();
        logger.info(String.format(LOG_START, configuration));
        for (var attribute : configuration.getAttributes()) {
            validationMap.put(
                attribute.getName(),
                Pair.of(
                    attribute.getDisplayName(), 
                    Pattern.compile(getRegexStringOrDefault(attribute))));
        }
        validateMap();
    }

    /**
     *
     * @param practitioner the Practitioner to save.
     * @return A new Practitioner
     */
    public String savePractitioner(Practitioner practitioner) {

        validateArgument(practitioner.getPrefix(), FIELD_NAME_PREFIX);
        validateArgument(practitioner.getGivenName(), FIELD_NAME_GIVEN_NAME);
        validateArgument(practitioner.getFamilyName(), FIELD_NAME_FAMILY_NAME);

        return fhirRepository.savePractitioner(toFhirPractitioner(practitioner));
    }

    private void validateArgument(String value, String argumentName) {
        var validation = validationMap.get(argumentName);
        if (value == null) {
            value = "";
        }
        if (!validation.getRight().matcher(value).matches()) {
            throw new ValidationException(String.format(ERROR_MESSAGE, validation.getLeft()));
        }
    }

    private org.hl7.fhir.r4.model.Practitioner toFhirPractitioner(Practitioner practitioner) {
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        fhirPractitioner.addName().setFamily(practitioner.getFamilyName()).addGiven(practitioner.getGivenName())
                .addPrefix(practitioner.getPrefix());
        fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);
        String id = UUID.randomUUID().toString();
        fhirPractitioner.setId(id);
        return fhirPractitioner;
    }

    private void validateMap() {
        if (validationMap.get(FIELD_NAME_PREFIX) == null
            || validationMap.get(FIELD_NAME_GIVEN_NAME) == null
            || validationMap.get(FIELD_NAME_FAMILY_NAME) == null) {
            throw new ServerError("Configuration field cannot be missing");
        }
    }

    private String getRegexStringOrDefault(PractitionerAttribute attribute) {
        if (attribute.getValidationRegex().isBlank()) {
            return REGEX_ALL;
        }
        return attribute.getValidationRegex();
    }
}
