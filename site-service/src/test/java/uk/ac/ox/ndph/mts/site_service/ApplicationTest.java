package uk.ac.ox.ndph.mts.site_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.Application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {
    final String strStartupName = "STARTUP";
    final String strStartupValue = "Staring site service...";

    @Test
    void messageAssert() {
        Application app = Application.STARTUP;
        assertThat(app.name(), equalTo(strStartupName));
        assertThat(app.message(), equalTo(strStartupValue));
    }

}
