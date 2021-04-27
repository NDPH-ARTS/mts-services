package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.practitionerserviceclient.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerUserAccountDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;

import java.util.List;
import java.util.function.Consumer;


@Service
public class PractitionerServiceInvoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PractitionerServiceInvoker.class);
    private PractitionerServiceClient practitionerServiceClient;
    private AzureTokenService azureTokenService;

    @Autowired
    public PractitionerServiceInvoker(final PractitionerServiceClient practitionerServiceClient,
                                      AzureTokenService azureTokenService) {
        this.practitionerServiceClient = practitionerServiceClient;
        this.azureTokenService = azureTokenService;
    }

    public void execute(List<PractitionerDTO> practitioners, String siteId) throws NullEntityException {
        if (practitioners == null) {
            throw new NullEntityException("No Practitioners in payload");
        }
        Consumer<HttpHeaders> authHeaders = PractitionerServiceClient.bearerAuth(azureTokenService.getToken());
        for (PractitionerDTO practitioner : practitioners) {
            LOGGER.info("Starting to create practitioner(s): {}", practitioner);
            practitioner.setUserSiteId(siteId);
            String practitionerId = practitionerServiceClient.createEntity(practitioner, authHeaders).getId();

            if (practitioner.getRoles() != null) {
                LOGGER.info("Assigning roles to practitioner(s): {} {}", practitionerId, practitioner.getRoles());
                for (String roleId : practitioner.getRoles()) {
                    RoleAssignmentDTO ra = new RoleAssignmentDTO(practitionerId, siteId, roleId);
                    practitionerServiceClient.assignRoleToPractitioner(ra, authHeaders);
                }
            }

            if (StringUtils.hasText(practitioner.getUserAccount())) {
                LOGGER.info("Linking practitioner: {} to user account: {}",
                        practitionerId, practitioner.getUserAccount());
                PractitionerUserAccountDTO userAccount = new PractitionerUserAccountDTO(practitionerId,
                        practitioner.getUserAccount());
                practitionerServiceClient.linkUserAccount(userAccount, authHeaders);
            } else {
                LOGGER.warn("Practitioner {} with no user account won't be able to login", practitionerId);
            }

        }
        LOGGER.info("Finished creating {} practitioner", practitioners.size());
    }
}
