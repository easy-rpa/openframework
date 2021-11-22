package eu.ibagroup.easyrpa.openframework.database.common;

import java.sql.SQLException;

/**
 * Represents a supplier of results.
 * @param <T> the type of results supplied by this supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws Exception;
}
