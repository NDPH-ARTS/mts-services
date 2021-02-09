package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.RoleAssignment;

import java.util.List;

@Service
public class PractitionerServiceInvoker extends ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PractitionerServiceInvoker.class);

    @Value("${practitioner.service}")
    private String practitionerService;

    @Value("${practitioner.service.endpoint.create}")
    private String createEndpoint;

    @Value("${practitioner.service.endpoint.assign.role}")
    private String assignRoleEndpoint;

    public PractitionerServiceInvoker() { }

    public PractitionerServiceInvoker(WebClient webClient) {
        super(webClient);
    }

    @Override
    protected String create(Entity practitioner) throws DependentServiceException {
        String uri = practitionerService + createEndpoint;
        IDResponse response = sendBlockingPostRequest(uri, practitioner, IDResponse.class);
        return response.getId();
    }

    protected void assignRoleToPractitioner(RoleAssignment roleAssignment) throws DependentServiceException {
        String uri = practitionerService + String.format(assignRoleEndpoint, roleAssignment.getPractitionerId());
        sendBlockingPostRequest(uri, roleAssignment, IDResponse.class);
    }


    public void execute(List<Practitioner> practitioners, String siteId) throws NullEntityException {
        if (practitioners != null) {
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

                LOGGER.info("Finished creating {} practitioner", practitioners.size());
            }
        } else {
            throw new NullEntityException("No Practitioners in payload");
        }
    }


}
