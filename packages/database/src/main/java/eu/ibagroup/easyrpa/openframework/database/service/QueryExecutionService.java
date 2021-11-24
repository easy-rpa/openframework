package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import eu.ibagroup.easyrpa.openframework.database.common.DatabaseSession;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.exceptions.ConnectionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryExecutionService {
    ConnectionKeeper connectionKeeper;

    QueryExecutionService(ConnectionKeeper connectionKeeper) {
        this.connectionKeeper = connectionKeeper;
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
        Statement statement = ConnectionKeeper.getSession().getStatement();
        ResultSet ret = (ResultSet) statement.executeQuery(sqlQuery);
        return ret;
    }

    public int executeUpdate(String sqlQuery) throws Exception {
        Statement statement = ConnectionKeeper.getSession().getStatement();
        int out = statement.executeUpdate(sqlQuery);
        return out;
    }

    public ResultSet executeInsert(String sqlQuery) throws Exception {
        Statement statement = ConnectionKeeper.getSession().getStatement();
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
        Statement statement = ConnectionKeeper.getSession().getStatement();
        for (String query : queries) {
            statement.addBatch(query);
        }
        batchResult = statement.executeBatch();
        return batchResult;
    }

    public void beginTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = ConnectionKeeper.getSession().getConnection();
        connection.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = ConnectionKeeper.getSession().getConnection();
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = ConnectionKeeper.getSession().getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }
////////////////////////////////////////////////////////////////////////////////////////

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
        return res;
    }

    public <T> List<T> query(QueryBuilder<T, Integer> queryBuilder, Class<T> clazz) throws SQLException {
        PreparedQuery<T> query = queryBuilder.prepare();
        List<T> res = getDao(clazz).query(query);
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> Dao.CreateOrUpdateStatus createOrUpdate(T entity) throws SQLException {
        Dao.CreateOrUpdateStatus res = getDao((Class<T>) entity.getClass()).createOrUpdate(entity);
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> int delete(T entity) throws SQLException {
        int res = getDao((Class<T>) entity.getClass()).delete(entity);
        return res;
    }

    public <T> int deleteById(int id, Class<T> clazz) throws SQLException {
        int res = getDao(clazz).deleteById(id);
        return res;
    }

    <R> void rollbackTransaction(Class<R> clazz) throws SQLException {
        getDao(clazz).rollBack(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection());
        getDao(clazz).setAutoCommit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection(), true);
    }

    <R> void commitTransaction(Class<R> clazz) throws SQLException {
        getDao(clazz).commit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection());
        getDao(clazz).setAutoCommit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection(), true);
    }

    <R> void beginTransaction(Class<R> clazz) throws SQLException {
        getDao(clazz).setAutoCommit(ConnectionKeeper.getOrmConnectionSource().getReadWriteConnection(), false);
    }

    private <T> Dao<T, Integer> getDao(Class<T> clazz) throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null) {
            throw new ConnectionException("Method must ba called inside lambdas 'withConnection()' or 'withTransaction()'");
        }
        return DaoManager.createDao(ConnectionKeeper.getOrmConnectionSource(), clazz);
    }

    public <T> QueryBuilder<T, Integer> getQueryBuilder(Class<T> clazz) throws SQLException {
        Dao<T, Integer> dao = getDao(clazz);
        return dao.queryBuilder();
    }

}
