package eu.ibagroup.easyrpa.openframework.database.function;

import eu.ibagroup.easyrpa.openframework.database.DatabaseConnection;

/**
 * TODO
 * <p>
 * A task that returns a result and may throw an exception.
 * Implementors define a single method with no arguments called
 * {@code call}.
 *
 * <p>The {@code Callable} interface is similar to {@link
 * Runnable}, in that both are designed for classes whose
 * instances are potentially executed by another thread.  A
 * {@code Runnable}, however, does not return a result and cannot
 * throw a checked exception.
 */
@FunctionalInterface
public interface DatabaseFunction<R> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    R apply(DatabaseConnection c) throws Exception;
}
