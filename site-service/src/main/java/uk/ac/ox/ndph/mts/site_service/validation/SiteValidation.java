package uk.ac.ox.ndph.mts.site_service.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfigurationProvider;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.model.Attribute;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implement a ModelEntityValidation for Site
 */
@Component
public class SiteValidation implements ModelEntityValidation<Site> {

    @Getter
    @Setter
    @AllArgsConstructor
    static class AttributeData {
        private String description;
        private Pattern regex;
        private Function<Site, String> value;
    }

    private static final String REGEX_ALL = ".*";

    private final Map<Attribute, AttributeData> validationMap;
    private final Logger logger = LoggerFactory.getLogger(SiteValidation.class);

    /**
     * @param configurationProvider provide Site Configuration
     */
    @Autowired
    public SiteValidation(SiteConfigurationProvider configurationProvider) {
        var configuration = configurationProvider.getConfiguration();
        validationMap = configuration.getAttributes().stream()
                .map(attribute -> Pair.of(attribute, Attribute.fromString(attribute.getName())))
                .collect(Collectors.toMap(Pair::getRight,
                    pair -> new AttributeData(pair.getLeft().getDisplayName(),
                                Pattern.compile(getRegexStringOrDefault(pair.getLeft().getValidationRegex())),
                                pair.getRight().getValue())));

        validateMap();
        logger.info(Validations.STARTUP.message(), configuration);
    }

    @Override
    public ValidationResponse validate(Site entity) {
        var validation = validateArgument(Attribute.NAME, entity);
        if (!validation.isValid()) {
            return validation;
        }
        validation = validateArgument(Attribute.ALIAS, entity);
        if (!validation.isValid()) {
            return validation;
        }
        validation = validateArgument(Attribute.PARENT_SITE_ID, entity);
        if (!validation.isValid()) {
            return validation;
        }
        validation = validateArgument(Attribute.SITE_TYPE, entity);
        if (!validation.isValid()) {
            return validation;
        }
        return new ValidationResponse(true, "");
    }

    private ValidationResponse validateArgument(Attribute attribute, Site site) {
        var validation = validationMap.get(attribute);
        var value = validation.getValue().apply(site);
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
        if (validationMap.get(Attribute.NAME) == null
                || validationMap.get(Attribute.ALIAS) == null
                    || validationMap.get(Attribute.PARENT_SITE_ID) == null
                        || validationMap.get(Attribute.SITE_TYPE) == null) {
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
