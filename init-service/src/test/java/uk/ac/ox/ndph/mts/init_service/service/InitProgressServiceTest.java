package uk.ac.ox.ndph.mts.init_service.service;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
public class InitProgressServiceTest {

    private final String filePath = "c:\\temp\\log.txt";

    @Test
    void testSubmitProgress() throws IOException {
        String expectedData = "Hello, world!";

        var initProgressService = new InitProgressService(filePath);
        initProgressService.submitProgress(expectedData);
        initProgressService.close();

        Path path = Paths.get(filePath);

        String read = Files.readString(path);
        MatcherAssert.assertThat(read, CoreMatchers.containsString(expectedData));
    }
}
