package uk.ac.ox.ndph.mts.practitioner_service.service;

<<<<<<< HEAD
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
=======
>>>>>>> main
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
<<<<<<< HEAD
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerAttribute;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ServerError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
=======
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;
>>>>>>> main

/**
 * Implement an EntityService interface.
 * Validation of practitioner based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class PractitionerService implements EntityService {
<<<<<<< HEAD
    private static final String REGEX_ALL = ".*";
    private static final String FIELD_NAME_PREFIX = "prefix";
    private static final String FIELD_NAME_GIVEN_NAME = "givenName";
    private static final String FIELD_NAME_FAMILY_NAME = "familyName";
    private static final String ERROR_MESSAGE = "value of argument %s cannot be empty";
    private static final String LOG_START = "Loaded practitioner service with configuration: {}";

    private final FhirRepository fhirRepository;    
    private final Map<String, Pair<String, Pattern>> validationMap;
=======

    private EntityStore<Practitioner> practitionerStore;
    private final ModelEntityValidation<Practitioner> entityValidation;
>>>>>>> main
    private final Logger logger = LoggerFactory.getLogger(PractitionerService.class);

    /**
     *
<<<<<<< HEAD
     * @param fhirRepository - FHIR repository interface
     * @param configurationProvider - provider of validation configuration
     */
    @Autowired
    public PractitionerService(FhirRepository fhirRepository,
        PractitionerConfigurationProvider configurationProvider) {
        this.fhirRepository = fhirRepository;
        validationMap = new HashMap<>();
        var configuration = configurationProvider.getConfiguration();
        logger.info(LOG_START, configuration);
        for (var attribute : configuration.getAttributes()) {
            validationMap.put(
                attribute.getName(),
                Pair.of(
                    attribute.getDisplayName(), 
                    Pattern.compile(getRegexStringOrDefault(attribute))));
        }
        validateMap();
=======
     * @param practitionerStore Practitioner store interface
     * @param entityValidation Practitioner validation interface 
     */
    @Autowired
    public PractitionerService(EntityStore<Practitioner> practitionerStore,
            ModelEntityValidation<Practitioner> entityValidation) {
        if (practitionerStore == null) {
            throw new InitialisationError("practitioner store cannot be null");
        }
        if (entityValidation == null) {
            throw new InitialisationError("entity validation cannot be null");
        }
        this.practitionerStore = practitionerStore;
        this.entityValidation = entityValidation;
        logger.info(Services.STARTUP.message());
>>>>>>> main
    }

    /**
     *
     * @param practitioner the Practitioner to save.
     * @return The id of the new practitioner
     */
    public String savePractitioner(Practitioner practitioner) {
<<<<<<< HEAD

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
=======
        var validationResponse = entityValidation.validate(practitioner);
        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
>>>>>>> main
        }
        return practitionerStore.saveEntity(practitioner);
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
