package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class GitRepo {

    @PostConstruct
    public void init() throws InvalidConfigurationException {
        try {
            Files.createDirectories(getRepoPath());

            Git git = Git.cloneRepository()
                .setURI("https://github.com/NDPH-ARTS/global-trial-config.git")
                .setDirectory(getRepoPath().toFile())
                .call();
        } catch (GitAPIException gitEx) {
            throw new InvalidConfigurationException(gitEx.getMessage());
        } catch (IOException ioEx) {
            throw new InvalidConfigurationException(ioEx.getMessage());
        }
    }

    private Repository getRepo() throws IOException {
        try (Git git = Git.open(getRepoPath().toFile())) {
            return git.getRepository();
        }
    }

    public byte[] getTrialFile(String fileName) {
        byte[] fileBytes;

        try {
            ObjectId lastCommitId = getRepo().resolve(Constants.HEAD);

            try (RevWalk revwalk = new RevWalk(getRepo())) {
                RevCommit commit = revwalk.parseCommit(lastCommitId);

                RevTree tree = commit.getTree();

                try (TreeWalk treeWalk = new TreeWalk(getRepo())) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(fileName));
                    if (!treeWalk.next()) {
                        throw new IllegalStateException("Did not find expected file");
                    }

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = getRepo().open(objectId);
                    loader.copyTo(System.out);

                    fileBytes = loader.getBytes();
                }

                revwalk.dispose();
            }
        } catch (IOException ioEx) {
            throw new InvalidConfigException(ioEx.getMessage());
        }

        return fileBytes;
    }

    private Path getRepoPath()  {
        //String strSource = this.getClass().getResource(File.separator).getPath();
        String strSource = "SOA_NDPH/mts-services/trial-config-service/target/classes/uk/ac/ox/ndph/mts/trial_config_service/";
        Path source = Paths.get(strSource);
        Path newFolder = Paths.get(source.toAbsolutePath() + File.separator + "config" + File.separator);

        source = Paths.get("resources");
        newFolder = Paths.get(source + File.separator + "config" + File.separator);

        return newFolder;
    }

    @PreDestroy
    public void deleteRepo() {
        Git.shutdown();

        try {
            FileUtils.deleteDirectory(getRepoPath().toFile());
        } catch (IOException ioEx) {
            throw new InvalidConfigException(ioEx.getMessage());
        }
    }

}
