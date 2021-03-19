package uk.ac.ox.ndph.mts.site_service.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SiteAddressTest {

    @Test
    void TestEmptyWhiteSpace() {
        // arrange
        final SiteAddress site = new SiteAddress("","","","","","","","");
        // assert
        assertThat(site.checkEmptyOrNull(), equalTo(true));
    }
}
