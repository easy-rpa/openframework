package eu.ibagroup.easyrpa.openframework.database;

import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.exceptions.DatabaseException;
import eu.ibagroup.easyrpa.openframework.database.function.DatabaseConsumer;
import eu.ibagroup.easyrpa.openframework.database.function.DatabaseFunction;

import javax.inject.Inject;

/**
 * Actually it's ORM Lite library flavored a bit to adapt using of it within RPA process.
 */
public class DatabaseService {

    private RPAServicesAccessor rpaServices;

    public DatabaseService() {
    }

    @Inject
    public DatabaseService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    public <T> T withConnection(String databaseAlias, DatabaseFunction<T> executor) {
        return withConnection(getForAlias(databaseAlias), executor);
    }

    public <T> T withConnection(DatabaseParams params, DatabaseFunction<T> executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            return (T) executor.apply(con);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("An error occurred during working with database.", e);
        }
    }

    public void withConnection(String databaseAlias, DatabaseConsumer executor) {
        withConnection(getForAlias(databaseAlias), executor);
    }

    public void withConnection(DatabaseParams params, DatabaseConsumer executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            executor.accept(con);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("An error occurred during working with database.", e);
        }
    }

    public <T> T withTransaction(String databaseAlias, DatabaseFunction<T> executor) {
        return withTransaction(getForAlias(databaseAlias), executor);
    }

    public <T> T withTransaction(DatabaseParams params, DatabaseFunction<T> executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            return con.callInTransaction(executor);
        }
    }

    public void withTransaction(String databaseAlias, DatabaseConsumer executor) {
        withTransaction(getForAlias(databaseAlias), executor);
    }

    public void withTransaction(DatabaseParams params, DatabaseConsumer executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            con.callInTransaction(executor);
        }
    }

    private DatabaseParams getForAlias(String databaseAlias) {
        if (rpaServices == null) {
            throw new IllegalStateException("This function can be used only with provided RPA services accessor. " +
                    "Use variant with direct passing of database params instead.");
        }
        DatabaseParams dbParams = rpaServices.getSecret(databaseAlias, DatabaseParams.class);
        if (dbParams == null) {
            throw new IllegalArgumentException(String.format("Getting of database params has failed. Secret vault entry " +
                    "with alias '%s' is not defined.", databaseAlias));
        }
        return dbParams;
    }
}
