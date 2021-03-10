package uk.ac.ox.ndph.mts.init_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class InitProgressService implements AutoCloseable {
    private BufferedWriter writer;

    @Autowired
    public InitProgressService(@Value("${INIT_PROGRESS_LOG_PATH}") String initLogMountPath) throws IOException {
        writer = new BufferedWriter(new FileWriter(initLogMountPath, true));
    }

    public void submitProgress(String text) throws IOException {
        writer.append(String.format("%s: %s%n", java.time.Clock.systemUTC().instant(), text));
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
