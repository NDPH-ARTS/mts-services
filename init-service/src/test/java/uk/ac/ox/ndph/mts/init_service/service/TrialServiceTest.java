package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.init_service.config.GitRepo;
import uk.ac.ox.ndph.mts.init_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.init_service.model.*;

import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrialServiceTest {

    TrialService trialService;

    @Mock
    GitRepo gitRepo;

    @Test
    void createTrialFromGitRepo() {
        byte[] trialBytes = getTrialBytes();
        when(gitRepo.getTrialFile(any())).thenReturn(trialBytes);
        trialService = new TrialService(gitRepo);
        assertEquals(mockedTrial().getTrialName(), trialService.createTrialFromGitRepo("any").getTrialName());
    }

    @Test
    void createTrialFromGitRepo_throwsInvalidConfigException() {
        Random rd = new Random();
        byte[] trialBytes = new byte[7];
        rd.nextBytes(trialBytes);
        when(gitRepo.getTrialFile(any())).thenReturn(trialBytes);
        trialService = new TrialService(gitRepo);
        assertThrows(InvalidConfigException.class, () -> trialService.createTrialFromGitRepo("any"));
    }

    private byte[] getTrialBytes() {

        ObjectMapper mapper = new ObjectMapper();
        byte[] trialBytes = null;
        try {
            trialBytes = mapper.writeValueAsBytes(mockedTrial());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return trialBytes;
    }

    Trial mockedTrial() {
        Trial trial = new Trial();
        trial.setTrialName("");

        Site site = new Site();
        site.setName("testSiteName");
        site.setAlias("testSiteAlias");

        Practitioner practitioner= new Practitioner();
        practitioner.setFamilyName("testFamilyName");
        practitioner.setGivenName("testGivenName");
        practitioner.setPrefix("Mr");

        Permission permission = new Permission();
        permission.setId("testPermission");

        Role role = new Role();
        role.setId("testId");
        role.setPermissions(Collections.singletonList(permission));

        trial.setSites(Collections.singletonList(site));
        trial.setPersons(Collections.singletonList(practitioner));
        trial.setRoles(Collections.singletonList(role));

        return trial;
    }
}
