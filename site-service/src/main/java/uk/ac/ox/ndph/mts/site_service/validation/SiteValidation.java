package uk.ac.ox.ndph.mts.site_service.validation;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.model.CoreAttribute;
import uk.ac.ox.ndph.mts.site_service.model.CustomAttribute;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;

import java.util.HashMap;
import java.util.List;
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
        private final Function<Site, Object> valueFunc;

        AttributeData(final String description, final Pattern regex, final Function<Site, Object> valueFunc) {
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

        public Object applyValueFunc(final Site site) {
            return this.valueFunc.apply(site);
        }

    }

    private static final String REGEX_ALL = ".*";

    private final Logger logger = LoggerFactory.getLogger(SiteValidation.class);
    private final Map<String, SiteConfiguration> configurationsOfSiteTypes = new HashMap<>();

    /**
     * Construct the site attribute validator. Note this class does not validate references between sites.
     *
     * @param configuration injected trial sites configuration
     */
    @Autowired
    public SiteValidation(final SiteConfiguration configuration) {
        // must validate between different sites
        Objects.requireNonNull(configuration, "site configuration cannot be null");

        Map<CoreAttribute, AttributeData> validationCoreMap;
        Map<CustomAttribute, AttributeData> validationCustomMap;

        // validates core attributes
        validationCoreMap = getCoreValidationMap(configuration);
        validateCoreMap(validationCoreMap);

        configurationsOfSiteTypes.put(configuration.getType(), configuration);

        List<SiteConfiguration> childSiteConfiguration = configuration.getChild();
        while (childSiteConfiguration != null) {
            validationCoreMap = getCoreValidationMap(childSiteConfiguration.get(0));
            validateCoreMap(validationCoreMap);
            configurationsOfSiteTypes.put(childSiteConfiguration.get(0).getType(), childSiteConfiguration.get(0));
            childSiteConfiguration = childSiteConfiguration.get(0).getChild();
        }

        logger.info(Validations.STARTUP.message(), configuration);
    }

    private Map<CoreAttribute, AttributeData> getCoreValidationMap(SiteConfiguration configuration) {
        Map<CoreAttribute, AttributeData> validationMap;
        validationMap = configuration.getAttributes().stream()
                .map(attribute -> Pair.of(attribute, CoreAttribute.fromString(attribute.getName())))
                .collect(Collectors.toMap(Pair::getRight,
                    pair -> new AttributeData(pair.getLeft().getDisplayName(),
                            Pattern.compile(getRegexStringOrDefault(pair.getLeft().getValidationRegex())),
                            pair.getRight().getValueFunc())));
        return validationMap;
    }

    @Override
    public ValidationResponse validateCoreAttributes(Site entity) {
        for (final CoreAttribute coreAttribute : CoreAttribute.values()) {
            final var validation = validateCoreArgument(coreAttribute, entity);
            if (!validation.isValid()) {
                return validation;
            }
        }
        return ok();
    }

    @Override
    public ValidationResponse validateCustomAttributes(Site entity) {
        SiteConfiguration siteConfiguration = configurationsOfSiteTypes.get(entity.getSiteType());

        final List<SiteAttributeConfiguration> customAttributes = siteConfiguration.getCustom();

        return validateCustomArgument(customAttributes, entity);
    }

    @Override
    public ValidationResponse validateExtAttributes(Site entity) {
        SiteConfiguration siteConfiguration = configurationsOfSiteTypes.get(entity.getSiteType());

        final List<SiteAttributeConfiguration> extAttributes = siteConfiguration.getExt();

        return validateExtArgument(extAttributes, entity);
    }

    private ValidationResponse validateCoreArgument(final CoreAttribute coreAttribute, final Site site) {

        if (configurationsOfSiteTypes.get(site.getSiteType()) == null) {
            return invalid(String.format(Validations.ERROR.message(), "Invalid Site"));
        }

        var validation = getCoreValidationMap(configurationsOfSiteTypes.get(site.getSiteType())).get(coreAttribute);

        var value = validation.applyValueFunc(site);
        if (value == null) {
            value = "";
        }
        if (!validation.getRegex().matcher(value.toString().trim()).matches()) {
            return invalid(String.format(Validations.ERROR.message(), validation.getDescription()));
        }
        return ok();
    }

    private ValidationResponse validateCustomArgument(List<SiteAttributeConfiguration> customAttributes,
                                                      final Site site) {
        if (customAttributes != null) {
            for (final SiteAttributeConfiguration attr : customAttributes) {
                if (attr.getType().equals("address") && site.getAddress() != null
                        && !site.getAddress().checkEmptyOrNull()) {
                    return ok();
                } else {
                    return invalid(String.format(Validations.ERROR.message(), "No Address in payload"));
                }
            }
        }

        if (customAttributes == null && site.getAddress() != null) {
            return invalid(String.format(Validations.ERROR.message(), "Cannot have Address"));
        }

        return ok();
    }

    private ValidationResponse validateExtArgument(List<SiteAttributeConfiguration> extAttributes,
                                                      final Site site) {
        if (extAttributes != null && site.getExtensions() != null) {

            boolean result = site.getExtensions().keySet().stream()
                    .allMatch(ext -> extAttributes.stream().anyMatch(attr -> attr.getName().equals(ext)));

            if (!result) {
                return invalid(String.format(Validations.ERROR.message(), "misconfigured extensions"));
            }
        }

        return ok();
    }

    /**
     * Validate the static (required) attributes configuration is present
     * @param validationMap
     */
    private void validateCoreMap(Map<CoreAttribute, AttributeData> validationMap) {
        for (final CoreAttribute attr : CoreAttribute.values()) {
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
