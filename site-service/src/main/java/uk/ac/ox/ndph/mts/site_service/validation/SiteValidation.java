package uk.ac.ox.ndph.mts.site_service.validation;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.model.Attribute;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.ac.ox.ndph.mts.site_service.model.ValidationResponse.invalid;
import static uk.ac.ox.ndph.mts.site_service.model.ValidationResponse.ok;

/**
 * Implement a ModelEntityValidation for Site
 */
@Component
public class SiteValidation implements ModelEntityValidation<Site> {

    static class AttributeData {
        private final String description;
        private final Pattern regex;
        private final Function<Site, String> valueFunc;

        AttributeData(final String description, final Pattern regex, final Function<Site, String> valueFunc) {
            this.description = description;
            this.regex = regex;
            this.valueFunc = valueFunc;
        }

        public String getDescription() {
            return this.description;
        }

        public Pattern getRegex() {
            return this.regex;
        }

        public String applyValueFunc(final Site site) {
            return this.valueFunc.apply(site);
        }

    }

    private static final String REGEX_ALL = ".*";

    private final Map<Attribute, AttributeData> validationMap;
    private final Logger logger = LoggerFactory.getLogger(SiteValidation.class);

    /**
     * Construct the site attribute validator. Note this class does not validate references between sites.
     *
     * @param configuration injected trial sites configuration
     */
    @Autowired
    public SiteValidation(final SiteConfiguration configuration) {
        Objects.requireNonNull(configuration, "site configuration cannot be null");
        this.validationMap = configuration.getAttributes().stream()
            .map(attribute -> Pair.of(attribute, Attribute.fromString(attribute.getName())))
            .collect(Collectors.toMap(Pair::getRight,
                pair -> new AttributeData(pair.getLeft().getDisplayName(),
                    Pattern.compile(getRegexStringOrDefault(pair.getLeft().getValidationRegex())),
                    pair.getRight().getValueFunc())));
        validateMap();
        logger.info(Validations.STARTUP.message(), configuration);
    }

    @Override
    public ValidationResponse validate(Site entity) {
        for (final Attribute attribute : Attribute.values()) {
            final var validation = validateArgument(attribute, entity);
            if (!validation.isValid()) {
                return validation;
            }
        }
        return ok();
    }

    private ValidationResponse validateArgument(final Attribute attribute, final Site site) {
        var validation = validationMap.get(attribute);
        var value = validation.applyValueFunc(site);
        if (value == null) {
            value = "";
        }
        if (!validation.getRegex().matcher(value.trim()).matches()) {
            return invalid(String.format(Validations.ERROR.message(), validation.getDescription()));
        }
        return ok();
    }

    /**
     * Validate the static (required) attributes configuration is present
     */
    private void validateMap() {
        for (final Attribute attr : Attribute.values()) {
            if (!validationMap.containsKey(attr)) {
                throw new InitialisationError(
                        String.format(Validations.MISSING_ATTRIBUTE.message(), attr.getAttributeName()));
            }
        }
    }

    private String getRegexStringOrDefault(String regexString) {
        if (regexString.isBlank()) {
            return REGEX_ALL;
        }
        return regexString;
    }

}
