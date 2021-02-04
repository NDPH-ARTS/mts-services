package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.RoleAssignment;

import java.util.List;

@Service
public class PractitionerServiceInvoker implements ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PractitionerServiceInvoker.class);

    @Value("${practitioner.service}")
    private String practitionerService;

    private final WebClient webClient;

    public PractitionerServiceInvoker() {
        this.webClient = WebClient.create(practitionerService);
    }
    public PractitionerServiceInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String create(Entity practitioner) throws DependentServiceException {
        try {
            IDResponse responseData = webClient.post()
                    .uri(practitionerService + "/practitioner")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(practitioner), Practitioner.class)
                    .retrieve()
                    .bodyToMono(IDResponse.class)//Question: Why does practitioner-service post endpoint return only the ID, not full Site data?
                    .block();
            return responseData.getId();
        } catch (Exception e) {
            LOGGER.info("FAILURE practitionerService {}", e.getMessage());
            throw new DependentServiceException("Error connecting to practitioner service");
        }
    }

    protected void assignRoleToPractitioner(RoleAssignment roleAssignment) throws DependentServiceException {
        try {
            webClient.post()
                    .uri(practitionerService + "/practitioner/"+roleAssignment.getPractitionerId()+"/roles")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(roleAssignment), RoleAssignment.class)
                    .retrieve()
                    .bodyToMono(IDResponse.class)
                    .block();
        } catch (Exception e) {
            LOGGER.info("FAILURE practitionerService {}", e.getMessage());
            throw new DependentServiceException("Error connecting to practitioner service to assign roles");
        }
    }



    public void execute(List<Practitioner> practitioners, String siteId) throws NullEntityException {
        if (practitioners != null) {
            for (Practitioner practitioner : practitioners) {
                LOGGER.info("Starting to create practitioner(s): {}", practitioner);
                String practitionerId = create(practitioner);

                LOGGER.info("Assigning roles to practitioner(s): {}", practitioner.getRoles());
                if(practitioner.getRoles()!=null){

                    for(String roleId : practitioner.getRoles()){
                        LOGGER.info("role assignment role id="+roleId+" siteid="+siteId+" practitionerID="+practitionerId);
                        RoleAssignment ra = new RoleAssignment(practitionerId, siteId, roleId);
                        assignRoleToPractitioner(ra);
                    }
                }

                LOGGER.info("Finished creating {} practitioner(s)", practitioners.size()+" practitionerId = "+practitionerId);
            }
        } else {
            throw new NullEntityException("No Practitioners in payload");
        }
    }


}
