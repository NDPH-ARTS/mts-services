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

    public PractitionerServiceInvoker() { }

    public PractitionerServiceInvoker(WebClient webClient) { super(webClient); }

    @Override
    protected String create(Entity practitioner) throws DependentServiceException {
        try {
            String createEndpoint = practitionerService + "/practitioner";
            IDResponse response = sendBlockingPostRequest(createEndpoint, practitioner, IDResponse.class);
            return response.getId();
        } catch (Exception e) {
            LOGGER.info("FAILURE at practitionerService create endpoint {}", e.getMessage());
            throw new DependentServiceException("Error connecting to practitioner service");
        }
    }

    protected void assignRoleToPractitioner(RoleAssignment roleAssignment) throws DependentServiceException {
        try {
            String uri = practitionerService + "/practitioner/" + roleAssignment.getPractitionerId() + "/roles";
            sendBlockingPostRequest(uri, roleAssignment, IDResponse.class);
        } catch (Exception e) {
            LOGGER.info("FAILURE at practitionerService assign roles endpoint {}", e.getMessage());
            throw new DependentServiceException("Error connecting to practitioner service to assign roles");
        }
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
