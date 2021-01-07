package uk.ac.ox.ndph.mts.trial_config_service.config;

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

    private static final String GIT_LOCATION = "gitRepo" + File.separator + "jsonConfig";

    @PostConstruct
    public void init() throws InvalidConfigException {
        if (!Files.exists(Paths.get(GIT_LOCATION))) {
            cloneRepository();
        }
    }

    private void cloneRepository() {
        try {
            Git.cloneRepository()
                .setURI("https://github.com/NDPH-ARTS/global-trial-config.git")
                .setDirectory(Paths.get(GIT_LOCATION).toFile())
                .call();
        } catch (GitAPIException gitEx) {
            throw new InvalidConfigException(gitEx.getMessage());
        }
    }

    private Repository getRepo() throws IOException {
        if (!Files.exists(Paths.get(GIT_LOCATION))) {
            cloneRepository();
        }

        try (Git git = Git.open(Paths.get(GIT_LOCATION).toFile())) {
            return git.getRepository();
        }
    }

    public byte[] getTrialFile(String fileName) {
        byte[] fileBytes;

        try {
            Repository repo =  getRepo();
            ObjectId lastCommitId = repo.resolve(Constants.HEAD);

            try (RevWalk revwalk = new RevWalk(repo)) {
                RevCommit commit = revwalk.parseCommit(lastCommitId);

                RevTree tree = commit.getTree();

                try (TreeWalk treeWalk = new TreeWalk(repo)) {
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

}
