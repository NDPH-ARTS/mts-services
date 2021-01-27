package uk.ac.ox.ndph.mts.init_service.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.ac.ox.ndph.mts.init_service.config.GitRepo;
import uk.ac.ox.ndph.mts.init_service.exception.InvalidConfigException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void invalidConfigExceptionTest(){
        assertThrows(InvalidConfigException.class, () -> gitRepo.cloneRepository());
    }


    @Test
    void illegalStatExceptionThrown() {
        assertThrows(IllegalStateException.class, () -> gitRepo.getTrialFile("x"));
    }

}