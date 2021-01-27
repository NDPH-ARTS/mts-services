package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.init_service.config.GitRepo;
import uk.ac.ox.ndph.mts.init_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.init_service.model.Trial;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class TrialService {

    @Value("${config.json}")
    private String config;

    private Trial trial;
    private GitRepo gitRepo;

    @Autowired
    public TrialService(GitRepo gitRepo) {
        this.gitRepo = gitRepo;
    }

    protected Trial createTrialFromGitRepo(String fileName) {
        Trial trial;

        try {
            byte[] fileBytes = gitRepo.getTrialFile(fileName);
            ObjectMapper objMapper = new ObjectMapper();
            trial = objMapper.readValue(fileBytes, Trial.class);
        } catch (IOException ioException) {
            throw new InvalidConfigException(ioException.getMessage());
        }

        return trial;
    }

    @PostConstruct
    public void init() throws InvalidConfigException {
        setTrial(createTrialFromGitRepo(config));
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }
}

