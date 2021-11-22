package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.common.CheckedSupplier;
import eu.ibagroup.easyrpa.openframework.database.common.DatabaseSession;
import eu.ibagroup.easyrpa.openframework.database.exceptions.ConnectionException;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DatabaseService {
    ConnectionKeeper connectionKeeper = null;

    RPAServicesAccessor rpaServices;

    abstract DatabaseService initPureConnection() throws SQLException, ClassNotFoundException;

    abstract DatabaseService initOrmConnection() throws SQLException;

    public DatabaseService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    private DatabaseSession getSession() throws SQLException, ClassNotFoundException {
        if (ConnectionKeeper.sessionAlive()) {
            return ConnectionKeeper.getSession();
        } else {
            throw new ConnectionException("Method must ba called inside lambdas 'withConnection()' or 'withTransaction()'");
        }

    }

    public void setSession(DatabaseSession session) throws SQLException {
        ConnectionKeeper.setSession(session);
    }

    private ConnectionSource getOrmConnectionSource() throws SQLException {
        return ConnectionKeeper.getOrmConnectionSource();
    }

    RPAServicesAccessor getRpaServices() {
        return rpaServices;
    }

    /**
     * *  Connection must be open until the user is working with the ResultSet
     * *  User is responsible for closing the connection: closeConnection()
     *
     * @param sqlQuery
     * @return
     * @throws Exception
     */
    public ResultSet executeQuery(String sqlQuery) throws Exception {
        Statement statement = getSession().getStatement();
        ResultSet ret = (ResultSet) statement.executeQuery(sqlQuery);
        return ret;
    }

    public int executeUpdate(String sqlQuery) throws Exception {
        Statement statement = getSession().getStatement();
        int out = statement.executeUpdate(sqlQuery);
        return out;
    }

    public ResultSet executeInsert(String sqlQuery) throws Exception {
        Statement statement = getSession().getStatement();
        statement.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);
        ResultSet keys = statement.getGeneratedKeys();
        return keys;
    }

    public int[] executeBatch(String... queriesArray) throws Exception {
        return executeBatch(new ArrayList<>(Arrays.asList(queriesArray)));
    }

    /**
     * Do not call this method with the SELECT query
     *
     * @param queries
     * @throws SQLException
     */
    public int[] executeBatch(List<String> queries) throws Exception {
        int[] batchResult;
        Statement statement = getSession().getStatement();
        for (String query : queries) {
            statement.addBatch(query);
        }
        batchResult = statement.executeBatch();
        return batchResult;
    }

    public void beginTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = getSession().getConnection();
        connection.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = getSession().getConnection();
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = getSession().getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }

    public <T> List<T> selectAll(Class<T> clazz) throws SQLException {
        return getDao(clazz).queryForAll();
    }

    @SuppressWarnings("unchecked")
    public <T> int create(T entity) throws SQLException {
        return getDao((Class<T>) entity.getClass()).create(entity);
    }

    @SuppressWarnings("unchecked")
    public <T> T createIfNotExists(T entity) throws SQLException {
        T res = getDao((Class<T>) entity.getClass()).createIfNotExists(entity);
        closeOrmConnection();
        return res;
    }

    public <T> List<T> query(QueryBuilder<T, Integer> queryBuilder, Class<T> clazz) throws SQLException {
        PreparedQuery<T> query = queryBuilder.prepare();
        List<T> res = getDao(clazz).query(query);
        closeOrmConnection();
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> Dao.CreateOrUpdateStatus createOrUpdate(T entity) throws SQLException {
        Dao.CreateOrUpdateStatus res = getDao((Class<T>) entity.getClass()).createOrUpdate(entity);
        closeOrmConnection();
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> int delete(T entity) throws SQLException {
        int res = getDao((Class<T>) entity.getClass()).delete(entity);
        closeOrmConnection();
        return res;
    }

    public <T> int deleteById(int id, Class<T> clazz) throws SQLException {
        int res = getDao(clazz).deleteById(id);
        closeOrmConnection();
        return res;
    }

    private <T> Dao<T, Integer> getDao(Class<T> clazz) throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null) {
            throw new ConnectionException("Method must ba called inside lambdas 'withConnection()' or 'withTransaction()'");
        }
        return DaoManager.createDao(ConnectionKeeper.getOrmConnectionSource(), clazz);

    }

    public <T> T withConnection(CheckedSupplier<T> executor) throws Exception {
        initPureConnection();
        try {
            T transactionResult = executor.get();
            return transactionResult;
        } catch (SQLException e) {
            throw e;
        } finally {
            closePureConnection();
        }
    }

    public <T, R> T withConnection(Class<R> entityClass, CheckedSupplier<T> executor) throws Exception {
        initOrmConnection();
        try {
            T transactionResult = executor.get();
            return transactionResult;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeOrmConnection();
        }
    }

    public <T> T withTransaction(CheckedSupplier<T> executor) throws Exception {
        initPureConnection();
        beginTransaction();
        try {
            T transactionResult = executor.get();
            commitTransaction();
            return transactionResult;
        } catch (SQLException e) {
            rollbackTransaction();
            throw e;
        } finally {
            closePureConnection();
        }
    }

    public <T, R> T withTransaction(Class<R> entityClass, CheckedSupplier<T> executor) throws Exception {
        initOrmConnection();
        beginTransaction(entityClass);
        try {
            T transactionResult = executor.get();
            commitTransaction(entityClass);
            return transactionResult;
        } catch (SQLException e) {
            rollbackTransaction(entityClass);
            throw e;
        } finally {
            closeOrmConnection();
        }
    }

    public <T> QueryBuilder<T, Integer> getQueryBuilder(Class<T> clazz) throws SQLException {
        ConnectionSource connectionSource = getOrmConnectionSource();
        if (connectionSource == null) {
            throw new ConnectionException("getQueryBuilder() method must ba called inside lambdas 'withConnection()' or 'withTransaction()'");
        }
        Dao<T, Integer> dao = DaoManager.createDao(connectionSource, clazz);
        return dao.queryBuilder();
    }

    private <R> void rollbackTransaction(Class<R> clazz) throws SQLException {
        getDao(clazz).rollBack(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection());
        getDao(clazz).setAutoCommit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection(), true);
    }

    private <R> void commitTransaction(Class<R> clazz) throws SQLException {
        getDao(clazz).commit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection());
        getDao(clazz).setAutoCommit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection(), true);
    }

    private <R> void beginTransaction(Class<R> clazz) throws SQLException {
        getDao(clazz).setAutoCommit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection(), false);
    }

    private <T> void closeAllConnections() throws Exception {
        closePureConnection();
        closeOrmConnection();
    }

    private void closeOrmConnection() throws SQLException {
        ConnectionKeeper.closeOrmConnection();
    }

    private void closePureConnection() throws Exception {
        ConnectionKeeper.closeSessionConnection();
    }

}
