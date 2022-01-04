package eu.ibagroup.easyrpa.openframework.database.function;

import eu.ibagroup.easyrpa.openframework.database.DatabaseConnection;

/**
 * TODO
 * <p>
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects.
 */
@FunctionalInterface
public interface DatabaseConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param c the input argument
     */
    void accept(DatabaseConnection c) throws Exception;
}
