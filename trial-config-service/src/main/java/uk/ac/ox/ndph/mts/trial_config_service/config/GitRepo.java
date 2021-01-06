package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class GitRepo {

    private final String gitLocation = "gitRepo"+ File.separator + "jsonConfig";

    @PostConstruct
    public void init() throws InvalidConfigException {
        try {
            if(!Files.exists(Paths.get(gitLocation))){
                Git git = Git.cloneRepository()
                    .setURI("https://github.com/NDPH-ARTS/global-trial-config.git")
                    .setDirectory(Paths.get(gitLocation).toFile())
                    .call();
            }
        } catch (GitAPIException gitEx) {
            throw new InvalidConfigException(gitEx.getMessage());
        }
    }

    private Repository getRepo() throws IOException {
        try (Git git = Git.open(Paths.get(gitLocation).toFile())) {
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

    public void destroy() {
        Git.shutdown();

        try {
            FileUtils.deleteDirectory(Paths.get(gitLocation).toFile());
        } catch (IOException ioEx) {
            throw new InvalidConfigException(ioEx.getMessage());
        }
    }

}
