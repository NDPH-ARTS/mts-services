package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.init_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.roleserviceclient.configuration.AzureTokenService;

import java.util.List;

@Service
public class PractitionerServiceInvoker extends ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PractitionerServiceInvoker.class);

    @Value("${practitioner-service.routes.create}")
    private String createEndpoint;

    @Value("${practitioner-service.routes.assign-role}")
    private String assignRoleEndpoint;

    @Value("${practitioner-service.routes.link-user-account}")
    private String linkUserAccountEndpoint;

    @Autowired
    public PractitionerServiceInvoker(final WebClient.Builder webClientBuilder,
                              @Value("${practitioner-service.uri}") String serviceUrlBase,
                              AzureTokenService azureTokenservice) {
        super(webClientBuilder, serviceUrlBase, azureTokenservice);
    }

    @Override
    protected String create(Entity practitioner) throws DependentServiceException {
        String uri = serviceUrlBase + createEndpoint;
        IDResponse response = sendBlockingPostRequest(uri, practitioner, IDResponse.class);
        return response.getId();
    }

    protected void assignRoleToPractitioner(RoleAssignment roleAssignment) throws DependentServiceException {
        String uri = serviceUrlBase + String.format(assignRoleEndpoint, roleAssignment.getPractitionerId());
        sendBlockingPostRequest(uri, roleAssignment, IDResponse.class);
    }

    protected void linkUserAccount(PractitionerUserAccount userAccount) throws DependentServiceException {
        String uri = serviceUrlBase + String.format(linkUserAccountEndpoint, userAccount.getPractitionerId());
        sendBlockingPostRequest(uri, userAccount, IDResponse.class);
    }

    public void execute(List<Practitioner> practitioners, String siteId) throws NullEntityException {
        if (practitioners == null) {
            throw new NullEntityException("No Practitioners in payload");
        }

        for (Practitioner practitioner : practitioners) {
            LOGGER.info("Starting to create practitioner(s): {}", practitioner);
            String practitionerId = create(practitioner);

            if (practitioner.getRoles() != null) {
                LOGGER.info("Assigning roles to practitioner(s): {} {}", practitionerId, practitioner.getRoles());
                for (String roleId : practitioner.getRoles()) {
                    RoleAssignment ra = new RoleAssignment(practitionerId, siteId, roleId);
                    assignRoleToPractitioner(ra);
                }
            }

            if (StringUtils.hasText(practitioner.getUserAccount())) {
                LOGGER.info("Linking practitioner: {} to user account: {}",
                        practitionerId, practitioner.getUserAccount());
                PractitionerUserAccount userAccount = new PractitionerUserAccount(practitionerId,
                        practitioner.getUserAccount());
                linkUserAccount(userAccount);
            } else {
                LOGGER.warn("Practitioner {} with no user account won't be able to login", practitionerId);
            }

        }
        LOGGER.info("Finished creating {} practitioner", practitioners.size());
    }
}
