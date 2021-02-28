package uk.ac.ox.ndph.mts.site_service.repository;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class EntityStoreTests {

    @Test
    void TestDefaultEntityStore_FindRoot_ReturnsEmpty() {
        // arrange
        final EntityStore<String, String> defaultStore = new EntityStore<>() {
            public String saveEntity(String entity) { return null; }
            public List<String> findAll() { return null; }
            public Optional<String> findById(String name) { return Optional.empty(); }
        };
        // act and assert
        assertThat(defaultStore.findRoot().isPresent(), equalTo(false));
        assertThat(defaultStore.existsByName("foo"), equalTo(false));
    }

}
