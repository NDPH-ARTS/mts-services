package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

//    @Test
//    void invalidConfigExceptionTest(){
//        assertThrows(InvalidConfigException.class, () -> gitRepo.cloneRepository());
//    }
//
//
//    @Test
//    void illegalStatExceptionThrown() {
//        assertThrows(IllegalStateException.class, () -> gitRepo.getTrialFile("x"));
//    }

}
