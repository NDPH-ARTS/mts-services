package uk.ac.ox.ndph.mts.site_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteAttribute;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.site_service.exception.ServerError;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;

/**
 * Implement an EntityService interface.
 * Validation of practitioner based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class SiteService implements EntityService {
    private static final String REGEX_ALL = ".*";
    private static final String FIELD_NAME_NAME = "name";
    private static final String FIELD_NAME_ALIAS = "alias";
    private static final String ERROR_MESSAGE = "value of argument %s cannot be empty";
    private static final String LOG_START = "Loaded practitioner service with configuration: {}";

    private final FhirRepository fhirRepository;    
    private final Map<String, Pair<String, Pattern>> validationMap;
    private final Logger logger = LoggerFactory.getLogger(SiteService.class);

    /**
     *
     * @param fhirRepository - FHIR repository interface
     * @param configurationProvider - provider of validation configuration
     */
    @Autowired
    public SiteService(FhirRepository fhirRepository,
        SiteConfigurationProvider configurationProvider) {
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
    }

    /**
     *
     * @param site the Site to save.
     * @return A new Site
     */
    public String saveSite(Site site) {

        validateArgument(site.getName(), FIELD_NAME_NAME);
        validateArgument(site.getAlias(), FIELD_NAME_ALIAS);

        return fhirRepository.saveSite(toFhirOrganization(site));
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

    private org.hl7.fhir.r4.model.Organization toFhirOrganization(Site site) {
        org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();
        fhirOrganization.setName(site.getName());
        fhirOrganization.addAlias(site.getAlias());

        String id = UUID.randomUUID().toString();
        fhirOrganization.setId(id);
        return fhirOrganization;
    }

    private void validateMap() {
        if (validationMap.get(FIELD_NAME_NAME) == null
            || validationMap.get(FIELD_NAME_ALIAS) == null) {
            throw new ServerError("Configuration field cannot be missing");
        }
    }

    private String getRegexStringOrDefault(SiteAttribute attribute) {
        if (attribute.getValidationRegex().isBlank()) {
            return REGEX_ALL;
        }
        return attribute.getValidationRegex();
    }
}
