package uk.ac.ox.ndph.mts.practitioner_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Holder for pageable result from the usual pageable API
 * @param <T>  type of entity in page
 */
public class PageableResult<T> implements Iterable<T> {

    private final List<T> content;

    private final PageableResultRequest request;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageableResult(@JsonProperty("content") final List<T> content, @JsonProperty("pageable") PageableResultRequest request) {
        Objects.requireNonNull(content, "content cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
        this.content = content;
        this.request = request;
    }

    @Override
    public Iterator<T> iterator() {
        return this.content.iterator();
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(this.content);
    }

    public Stream<T> stream() {
        return this.content.stream();
    }

    public PageableResultRequest geRequest() {
        return this.request;
    }

    public static <T> PageableResult<T> empty() {
        return new PageableResult<>(Collections.emptyList(), new PageableResultRequest(0, 10));
    }

    public static final class PageableResultRequest {

        private final int pageNumber;
        private final int pageSize;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public PageableResultRequest(@JsonProperty("pageNumber") final int pageNumber, @JsonProperty("pageSize") final int pageSize) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
        }

        public int getPageNumber() {
            return this.pageNumber;
        }

        public int getPageSize() {
            return this.pageSize;
        }

    }

}

