package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitRepoTest {

    GitRepo gitRepo = new GitRepo();

    @BeforeAll
    void setUp() throws InvalidConfigException {
        gitRepo.init();
    }

    @Test
    void getTrialFile() throws InvalidConfigException {
        assertNotNull(gitRepo.getTrialFile("config.json"));
    }

}
