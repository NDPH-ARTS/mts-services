package uk.ac.ox.ndph.mts.practitioner_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Holder for pageable result from the usual pageable API
 * @param <T>  type of entity in page
 */
public class PageableResult<T> implements Iterable<T> {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final Collection<T> content;

    private final PageableResultRequest pageable;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageableResult(@JsonProperty("content") final Collection<T> content,
                          @JsonProperty("pageable") PageableResultRequest pageable) {
        Objects.requireNonNull(content, "content cannot be null");
        Objects.requireNonNull(pageable, "pageable cannot be null");
        this.content = content;
        this.pageable = pageable;
    }

    @Override
    public Iterator<T> iterator() {
        return this.content.iterator();
    }

    @JsonProperty("content")
    public Collection<T> getContent() {
        return Collections.unmodifiableCollection(this.content);
    }

    public Stream<T> stream() {
        return this.content.stream();
    }

    @JsonProperty("pageable")
    public PageableResultRequest getPageable() {
        return this.pageable;
    }

    public static <T> PageableResult<T> empty() {
        return new PageableResult<>(Collections.emptyList(), new PageableResultRequest(0, DEFAULT_PAGE_SIZE));
    }

    public static <T> PageableResult<T> singleton(final T t) {
        return new PageableResult<>(Collections.singletonList(t), new PageableResultRequest(0, DEFAULT_PAGE_SIZE));
    }

    public static <T> PageableResult<T> from(final Collection<T> t) {
        return new PageableResult<>(t, new PageableResultRequest(0, t.size()));
    }

    public static final class PageableResultRequest {

        private final int pageNumber;
        private final int pageSize;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public PageableResultRequest(
                @JsonProperty("pageNumber") final int pageNumber,
                @JsonProperty("pageSize") final int pageSize) {
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

