package eu.ibagroup.easyrpa.openframework.database.function;

import eu.ibagroup.easyrpa.openframework.database.DatabaseConnection;

/**
 * Represents actions that need to be performed using database connection with returning of specific result.
 *
 * @param <R> the type of returning result.
 */
@FunctionalInterface
public interface DatabaseFunction<R> {

    /**
     * Performs actions on the given database connection.
     *
     * @param c object that represents database connection and provides functionality to perform different queries
     *          and actions.
     * @return computed result.
     * @throws Exception in case of some errors.
     * @see DatabaseConnection
     */
    R apply(DatabaseConnection c) throws Exception;
}
