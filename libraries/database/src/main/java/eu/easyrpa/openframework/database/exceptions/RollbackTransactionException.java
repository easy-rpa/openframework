package eu.easyrpa.openframework.database.exceptions;

/**
 * Special type of exception to cancel transaction.
 * <p>
 * It should be thrown by user when is necessary to simple cancel transaction without throwing of errors and
 * continue code execution.
 */
public class RollbackTransactionException extends RuntimeException {

    /**
     * Constructs a new rollback transaction exception.
     */
    public RollbackTransactionException() {
        super("Transaction has been canceled.");
    }
}
