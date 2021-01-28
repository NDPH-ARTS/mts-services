package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfigurationProvider;
import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.model.Attribute;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PractitionerValidation implements ModelEntityValidation<Practitioner> {

    static class AttributeData {
        private String description;
        private Pattern regex;
        private Function<Practitioner, String> getValue;

        AttributeData(final String description, final Pattern regex, final Function<Practitioner, String> getValue) {
            this.description = description;
            this.regex = regex;
            this.getValue = getValue;
        }

        public Function<Practitioner, String> getGetValue() {
            return getValue;
        }

        public Pattern getRegex() {
            return regex;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final String REGEX_ALL = ".*";

    private final Map<Attribute, AttributeData> validationMap;
    @SuppressWarnings("FieldCanBeLocal")
    private final Logger logger = LoggerFactory.getLogger(PractitionerValidation.class);

    /**
     * @param configurationProvider provide Practitioner Configuration
     */
    @Autowired
    public PractitionerValidation(PractitionerConfigurationProvider configurationProvider) {
        var configuration = configurationProvider.getConfiguration();
        validationMap = configuration.getAttributes().stream()
                .map(attribute -> Pair.of(attribute, Attribute.fromString(attribute.getName())))
                .collect(Collectors.toMap(Pair::getRight,
                    pair -> new AttributeData(pair.getLeft().getDisplayName(),
                                Pattern.compile(getRegexStringOrDefault(pair.getLeft().getValidationRegex())),
                                pair.getRight().getGetValue())));

        validateMap();
        logger.info(Validations.STARTUP.message(), "Practitioner", configuration);
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
                    String.format(Validations.ERROR.message(), validation.getDescription()));
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
