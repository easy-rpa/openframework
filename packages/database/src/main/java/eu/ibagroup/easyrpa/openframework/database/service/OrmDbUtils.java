package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.exceptions.ConnectionException;

import java.sql.SQLException;
import java.util.List;

public class OrmDbUtils {
    ConnectionKeeper connectionKeeper;

    OrmDbUtils(ConnectionKeeper connectionKeeper) {
        this.connectionKeeper = connectionKeeper;
    }

    public <T> List<T> selectAll(Class<T> clazz) throws SQLException {
        return getDao(clazz).queryForAll();
    }

    @SuppressWarnings("unchecked")
    public <T> int create(T entity) throws SQLException {
        return getDao((Class<T>) entity.getClass()).create(entity);
    }

    public <T> int createTable(Class<T> clazz) throws SQLException {
        return TableUtils.createTable(getDao(clazz).getConnectionSource(), clazz);
    }

    public <T> int createTableIfNotExists(Class<T> clazz) throws SQLException {
        return TableUtils.createTableIfNotExists(getDao(clazz).getConnectionSource(), clazz);
    }

    public <T> int clearTable(Class<T> clazz) throws SQLException {
        return TableUtils.clearTable(getDao(clazz).getConnectionSource(), clazz);
    }

    public <T> int dropTable(Class<T> clazz) throws SQLException {
        int i = dropTable(clazz, true);
        return i;
    }

    public <T> int dropTable(Class<T> clazz, boolean ignoreErrors) throws SQLException {
        int i = TableUtils.dropTable(getDao(clazz).getConnectionSource(),clazz, ignoreErrors);
        return i;
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

    void closeConnection() throws SQLException {
        ConnectionKeeper.closeOrmConnection();
    }
}
