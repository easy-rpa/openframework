package eu.ibagroup.easyrpa.openframework.database.service;

import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.function.CheckedFunction;
import eu.ibagroup.easyrpa.openframework.database.common.DatabaseSession;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;

import java.sql.SQLException;

public abstract class DatabaseService {
    ConnectionKeeper connectionKeeper = null;

    RPAServicesAccessor rpaServices;
    String connectionString;
    String userName;
    String password;


    abstract DatabaseService initJdbcConnection() throws SQLException, ClassNotFoundException;

    abstract DatabaseService initOrmConnection() throws SQLException;

    public DatabaseService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    public DatabaseService(String connectionString, String userName, String password) {
        this.rpaServices = null;
        this.connectionString = connectionString;
        this.userName = userName;
        this.password = password;
    }

    protected void setSession(DatabaseSession session) throws SQLException {
        ConnectionKeeper.setSession(session);
    }

    RPAServicesAccessor getRpaServices() {
        return rpaServices;
    }

    public <T> T withConnection(CheckedFunction<QueryDbUtils, T> executor) throws Exception {

        initJdbcConnection();
        QueryDbUtils service = new QueryDbUtils(this.connectionKeeper);
        try {
            return (T) executor.apply(service);
        } catch (Exception e) {
            throw e;
        } finally {
            service.closeConnection();
        }
    }

    public <T> T withTransaction(CheckedFunction<QueryDbUtils, T> executor) throws Exception {
        initJdbcConnection();
        QueryDbUtils service = new QueryDbUtils(this.connectionKeeper);
        service.beginTransaction();
        try {
            T transactionResult = executor.apply(service);
            service.commitTransaction();
            return transactionResult;
        } catch (SQLException e) {
            service.rollbackTransaction();
            throw e;
        } finally {
            service.closeConnection();
        }
    }

    public <T, R> T withConnection(Class<R> entityClass, CheckedFunction<OrmDbUtils, T> executor) throws Exception {
        initOrmConnection();
        OrmDbUtils service = new OrmDbUtils(this.connectionKeeper);
        try {
            return executor.apply(service);
        } catch (SQLException e) {
            throw e;
        } finally {
            service.closeConnection();
        }
    }

    public <T, R> T withTransaction(Class<R> entityClass, CheckedFunction<OrmDbUtils, T> executor) throws Exception {
        initOrmConnection();
        OrmDbUtils service = new OrmDbUtils(this.connectionKeeper);
        service.beginTransaction(entityClass);
        try {
            T transactionResult = executor.apply(service);
            service.commitTransaction(entityClass);
            return transactionResult;
        } catch (SQLException e) {
            service.rollbackTransaction(entityClass);
            throw e;
        } finally {
            service.closeConnection();
        }
    }
}
