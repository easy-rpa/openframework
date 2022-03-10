package eu.ibagroup.easyrpa.openframework.database.exceptions;

/**
 * Database runtime exception. Thrown in case of some errors or problems during working with database.
 */
public class DatabaseException extends RuntimeException {

    /**
     * Constructs a new database exception with the specified detail message and
     * cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new database exception with the specified cause.
     *
     * @param cause the cause.
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
