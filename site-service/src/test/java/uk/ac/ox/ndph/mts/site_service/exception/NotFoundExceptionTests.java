package uk.ac.ox.ndph.mts.site_service.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class NotFoundExceptionTests {

    @Test
    void TestGetId_withId_returnsId() {
        final var id = "this-is-the-id";
        final NotFoundException exception = new NotFoundException("message", id, new Exception());
        assertThat(exception.getId(), equalTo(id));
    }

}
