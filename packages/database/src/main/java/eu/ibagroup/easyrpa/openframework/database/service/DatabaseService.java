package eu.ibagroup.easyrpa.openframework.database.service;

import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.function.CheckedFunction;
import eu.ibagroup.easyrpa.openframework.database.common.DatabaseSession;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;

import java.sql.SQLException;

public abstract class DatabaseService {
    ConnectionKeeper connectionKeeper = null;

    RPAServicesAccessor rpaServices;

    abstract DatabaseService initJdbcConnection() throws SQLException, ClassNotFoundException;

    abstract DatabaseService initOrmConnection() throws SQLException;

    public DatabaseService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    protected void setSession(DatabaseSession session) throws SQLException {
        ConnectionKeeper.setSession(session);
    }

    RPAServicesAccessor getRpaServices() {
        return rpaServices;
    }

    public <T> T withConnection(CheckedFunction<QueryExecutionService, T> executor) throws Exception {

        initJdbcConnection();
        QueryExecutionService service = new QueryExecutionService(this.connectionKeeper);
        try {
            T transactionResult = (T) executor.apply(service);
            return transactionResult;
        } catch (Exception e) {
            throw e;
        } finally {
            closeJdbcConnection();
        }
    }

    public <T> T withTransaction(CheckedFunction<QueryExecutionService, T> executor) throws Exception {
        initJdbcConnection();
        QueryExecutionService service = new QueryExecutionService(this.connectionKeeper);
        service.beginTransaction();
        try {
            T transactionResult = executor.apply(service);
            service.commitTransaction();
            return transactionResult;
        } catch (SQLException e) {
            service.rollbackTransaction();
            throw e;
        } finally {
            closeJdbcConnection();
        }
    }

    public <T, R> T withConnection(Class<R> entityClass, CheckedFunction<QueryExecutionService, T> executor) throws Exception {
        initOrmConnection();
        QueryExecutionService service = new QueryExecutionService(this.connectionKeeper);
        try {
            T transactionResult = executor.apply(service);
            return transactionResult;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeOrmConnection();
        }
    }

    public <T, R> T withTransaction(Class<R> entityClass, CheckedFunction<QueryExecutionService, T> executor) throws Exception {
        initOrmConnection();
        QueryExecutionService service = new QueryExecutionService(this.connectionKeeper);
        service.beginTransaction(entityClass);
        try {
            T transactionResult = executor.apply(service);
            service.commitTransaction(entityClass);
            return transactionResult;
        } catch (SQLException e) {
            service.rollbackTransaction(entityClass);
            throw e;
        } finally {
            closeOrmConnection();
        }
    }

    private void closeOrmConnection() throws SQLException {
        ConnectionKeeper.closeOrmConnection();
    }

    private void closeJdbcConnection() throws Exception {
        ConnectionKeeper.closeSessionConnection();
    }

}
