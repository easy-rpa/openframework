package eu.ibagroup.easyrpa.openframework.database.function;

import eu.ibagroup.easyrpa.openframework.database.DatabaseConnection;

/**
 * Represents actions that need to be performed using database connection without returning of any result.
 */
@FunctionalInterface
public interface DatabaseConsumer {

    /**
     * Performs actions on the given database connection.
     *
     * @param c object that represents database connection and provides functionality to perform different queries
     *          and actions.
     * @throws Exception in case of some errors.
     * @see DatabaseConnection
     */
    void accept(DatabaseConnection c) throws Exception;
}
