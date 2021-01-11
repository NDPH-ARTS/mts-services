package uk.ac.ox.ndph.mts.practitioner_service.validation;

import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ox.ndph.mts.practitioner_service.Consts;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfigurationProvider;
import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.model.Attribute;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

/**
 * Implement a ModelEntityValidation for Practitioner
 */
@Component
public class PractitionerValidation implements ModelEntityValidation<Practitioner> {

    @Getter
    @Setter
    @AllArgsConstructor
    class AttributeData {
        private String description;
        private Pattern regex;
        private Function<Practitioner, String> getValue;
    }

    private static final String REGEX_ALL = ".*";

    private final Map<Attribute, AttributeData> validationMap;
    private final Logger logger = LoggerFactory.getLogger(PractitionerValidation.class);

    /**
     * @param configurationProvider provide Practitioner Configuration
     */
    @Autowired
    public PractitionerValidation(PractitionerConfigurationProvider configurationProvider) {
        var configuration = configurationProvider.getConfiguration();
        validationMap = configuration.getAttributes().stream()
                .map(attribute -> Pair.of(attribute, Attribute.fromString(attribute.getName())))
                .collect(Collectors.toMap(pair -> pair.getRight(),
                    pair -> new AttributeData(pair.getLeft().getDisplayName(),
                                Pattern.compile(getRegexStringOrDefault(pair.getLeft().getValidationRegex())),
                                pair.getRight().getGetValue())));

        validateMap();
        logger.info(Consts.VALIDATION_STARTUP_LOG.getValue(), configuration);
    }

    @Override
    public ValidationResponse validate(Practitioner entity) {
        var validation = validateArgument(Attribute.PREFIX, entity);
        if (!validation.isValid()) {
            return validation;
        }
        validation = validateArgument(Attribute.GIVEN_NAME, entity);
        if (!validation.isValid()) {
            return validation;
        }
        validation = validateArgument(Attribute.FAMILY_NAME, entity);
        if (!validation.isValid()) {
            return validation;
        }
        return new ValidationResponse(true, "");
    }

    private ValidationResponse validateArgument(Attribute attribute, Practitioner practitioner) {
        var validation = validationMap.get(attribute);
        var value = validation.getGetValue().apply(practitioner);
        if (value == null) {
            value = "";
        }
        if (!validation.getRegex().matcher(value).matches()) {
            return new ValidationResponse(false,
                    String.format(Consts.VALIDATION_ERROR_MESSAGE.getValue(), validation.getDescription()));
        }
        return new ValidationResponse(true, "");
    }

    private void validateMap() {
        if (validationMap.get(Attribute.PREFIX) == null || validationMap.get(Attribute.GIVEN_NAME) == null
                || validationMap.get(Attribute.FAMILY_NAME) == null) {
            throw new InitialisationError("Configuration field cannot be missing");
        }
    }

    private String getRegexStringOrDefault(String regexString) {
        if (regexString.isBlank()) {
            return REGEX_ALL;
        }
        return regexString;
    }
}
