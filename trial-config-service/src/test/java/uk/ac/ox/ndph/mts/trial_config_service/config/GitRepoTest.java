package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitRepoTest {

    GitRepo gitRepo = new GitRepo();

    @BeforeAll
    void setUp() throws GitAPIException, IOException {
        gitRepo.init();
    }

    @Test
    void getTrialFile() throws IOException {
        assertNotNull(gitRepo.getTrialFile("config.json"));
    }

    @AfterAll
    void tearDown() throws IOException {
        gitRepo.deleteRepo();
    }

}
