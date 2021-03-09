package uk.ac.ox.ndph.mts.security.authorisation;

import uk.ac.ox.ndph.mts.security.exception.AuthorisationException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a list of objects that are filterable by site ID/parent site ID.
 * You can remove instances from the iterable but not mutate them
 */
public interface FilterableSites<T> extends Iterable<T> {

    /**
     * Get the parent site ID for an instance in my list
     *
     * @param value instance on list, not null
     * @return parent site ID, might be null
     */
    String getParentSiteId(T value);

    /**
     * Get the site ID for an instance in my list
     *
     * @param value instance on list, not null
     * @return site ID, never null
     */
    String getSiteId(T value);

    /**
     * Filter by predicate method
     *
     * @param pred if returns true, then remove
     * @return true if any removed
     */
    default boolean removeIf(final Predicate<? super T> pred) {
        boolean removed = false;
        for (final Iterator<T> i = iterator(); i.hasNext();) {
            if (pred.test(i.next())) {
                i.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Remove all elements, default method uses removeIf
     * @return true if any removed
     */
    default boolean removeEvery() {
        return removeIf(t -> true);
    }

    /**
     * Stream method makes a stream from the iterator by default. Prefer to override this
     * if implementation has access to a source streamable
     * @return stream on iterator
     */
    default Stream<T> stream() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                iterator(),
                Spliterator.ORDERED), false);
    }

    static <T> FilterableSites<T> fromMethodNames(final Collection<T> source,
                                                  final String getParentSiteIdMethodName,
                                                  final String getSiteIdMethodName) {
        return new FilterableSites<>() {

            @Override
            public Iterator<T> iterator() {
                return source.iterator();
            }

            @Override
            public void forEach(final Consumer<? super T> action) {
                source.forEach(action);
            }

            @Override
            public Stream<T> stream() {
                return source.stream();
            }

            @Override
            public boolean removeIf(final Predicate<? super T> pred) {
                return source.removeIf(pred);
            }

            private String invokeMethodNamed(final T value, final String methodName) {
                try {
                    final Object result = value.getClass().getMethod(methodName).invoke(value);
                    return (result == null)  ? null : result.toString();
                } catch (Exception e) {
                    throw new AuthorisationException(
                        "Error getting ID using method: " + methodName, e);
                }
            }

            @Override
            public String getParentSiteId(final T value) {
                return invokeMethodNamed(value, getParentSiteIdMethodName);
            }

            @Override
            public String getSiteId(T value) {
                return invokeMethodNamed(value, getSiteIdMethodName);
            }

            @Override
            public String toString() {
                return source.toString();
            }

        };
    }

}
