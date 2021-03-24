package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class InitProgressReporter implements AutoCloseable {
    private BufferedWriter writer;
    private static final Logger LOGGER = LoggerFactory.getLogger(InitProgressReporter.class);

    @Autowired
    public InitProgressReporter(@Value("${progress.log.path:}") String initLogMountPath) throws IOException {
        LOGGER.info(String.format("initializing InitProgressReporter with log file [%s]", initLogMountPath));
        if (StringUtils.hasText(initLogMountPath)) {
            writer = new BufferedWriter(new FileWriter(initLogMountPath, true));
        }
    }

    public void submitProgress(String text) throws IOException {
        LOGGER.info(text);
        if (writer != null) {
            writer.append(String.format("%s: %s%n", java.time.Clock.systemUTC().instant(), text));
            writer.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
