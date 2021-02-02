package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;

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
    public String send(Entity practitioner) throws DependentServiceException {
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


    public void execute(List<Practitioner> practitioners) throws NullEntityException {
        if (practitioners != null) {
            for (Entity practitioner : practitioners) {
                LOGGER.info("Starting to create practitioner(s): {}", practitioner);
                String practitionerId = send(practitioner);
                LOGGER.info("Finished creating {} practitioner(s)", practitioners.size()+" practitionerId returned = "+practitionerId);

            }
        } else {
            throw new NullEntityException("No Practitioners in payload");
        }
    }


}
