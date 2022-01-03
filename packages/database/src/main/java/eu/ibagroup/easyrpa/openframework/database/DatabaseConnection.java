package eu.ibagroup.easyrpa.openframework.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.table.TableUtils;
import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import eu.ibagroup.easyrpa.openframework.database.exceptions.DatabaseException;
import eu.ibagroup.easyrpa.openframework.database.exceptions.RollbackTransactionException;
import eu.ibagroup.easyrpa.openframework.database.function.DatabaseConsumer;
import eu.ibagroup.easyrpa.openframework.database.function.DatabaseFunction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseConnection implements AutoCloseable {

    private JdbcPooledConnectionSource connectionSource;

    DatabaseConnection(DatabaseParams dbParams) {
        try {
            connectionSource = new JdbcPooledConnectionSource(dbParams.getJdbcUrl(), dbParams.getUser(), dbParams.getPassword());
        } catch (Exception e) {
            throw new DatabaseException("JDBC connection initialization has failed.", e);
        }
    }

    public <T> List<T> selectAll(Class<T> entityClass) {
        try {
            return getDao(entityClass).queryForAll();
        } catch (Exception e) {
            throw new DatabaseException(String.format("Selecting of all '%s' entities has failed.", entityClass.getName()), e);
        }
    }

    public <T> int create(T entity) {
        if (entity != null) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entity.getClass();
            try {
                return getDao(entityClass).create(entity);
            } catch (Exception e) {
                throw new DatabaseException(String.format("Creating of entity '%s' has failed.", entityClass.getName()), e);
            }
        }
        return 0;
    }

    public <T> int create(Collection<T> entities) {
        if (entities != null && entities.size() > 0) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entities.iterator().next().getClass();
            try {
                Dao<T, ?> dao = getDao(entityClass);
                return dao.callBatchTasks(() -> {
                    int counter = 0;
                    for (T entity : entities) {
                        counter += dao.create(entity);
                    }
                    return counter;
                });
            } catch (Exception e) {
                throw new DatabaseException(String.format("Creating of entities '%s' has failed.", entityClass.getName()), e);
            }
        }
        return 0;
    }

    public <T> T createIfNotExists(T entity) {
        if (entity != null) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entity.getClass();
            try {
                return getDao(entityClass).createIfNotExists(entity);
            } catch (Exception e) {
                throw new DatabaseException(String.format("Creating of entity '%s' has failed.", entityClass.getName()), e);
            }
        }
        return null;
    }

    public <T> List<T> createIfNotExists(Collection<T> entities) {
        List<T> results = new ArrayList<>();
        if (entities != null && entities.size() > 0) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entities.iterator().next().getClass();
            try {
                Dao<T, ?> dao = getDao(entityClass);
                dao.callBatchTasks(() -> {
                    for (T entity : entities) {
                        results.add(dao.createIfNotExists(entity));
                    }
                    return null;
                });
            } catch (Exception e) {
                throw new DatabaseException(String.format("Creating of entities '%s' has failed.", entityClass.getName()), e);
            }
        }
        return results;
    }

    public <T> Dao.CreateOrUpdateStatus createOrUpdate(T entity) {
        if (entity != null) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entity.getClass();
            try {
                return getDao(entityClass).createOrUpdate(entity);
            } catch (Exception e) {
                throw new DatabaseException(String.format("Updating of entity '%s' has failed.", entityClass.getName()), e);
            }
        }
        return new Dao.CreateOrUpdateStatus(false, false, 0);
    }

    public <T> List<Dao.CreateOrUpdateStatus> createOrUpdate(Collection<T> entities) {
        List<Dao.CreateOrUpdateStatus> results = new ArrayList<>();
        if (entities != null && entities.size() > 0) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entities.iterator().next().getClass();
            try {
                Dao<T, ?> dao = getDao(entityClass);
                dao.callBatchTasks(() -> {
                    for (T entity : entities) {
                        results.add(dao.createOrUpdate(entity));
                    }
                    return null;
                });
            } catch (Exception e) {
                throw new DatabaseException(String.format("Updating of entities '%s' has failed.", entityClass.getName()), e);
            }
        }
        return results;
    }

    public <T> int update(T entity) {
        if (entity != null) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entity.getClass();
            try {
                return getDao(entityClass).update(entity);
            } catch (Exception e) {
                throw new DatabaseException(String.format("Updating of entity '%s' has failed.", entityClass.getName()), e);
            }
        }
        return 0;
    }

    public <T> int update(Collection<T> entities) {
        if (entities != null && entities.size() > 0) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entities.iterator().next().getClass();
            try {
                Dao<T, ?> dao = getDao(entityClass);
                return dao.callBatchTasks(() -> {
                    int counter = 0;
                    for (T entity : entities) {
                        counter += dao.update(entity);
                    }
                    return counter;
                });
            } catch (Exception e) {
                throw new DatabaseException(String.format("Updating of entities '%s' has failed.", entityClass.getName()), e);
            }
        }
        return 0;
    }

    public <T> int delete(T entity) {
        if (entity != null) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entity.getClass();
            try {
                return getDao(entityClass).delete(entity);
            } catch (Exception e) {
                throw new DatabaseException(String.format("Deleting of entity '%s' has failed.", entityClass.getName()), e);
            }
        }
        return 0;
    }

    public <T> int delete(Collection<T> entities) {
        if (entities != null && entities.size() > 0) {
            //noinspection unchecked
            Class<T> entityClass = (Class<T>) entities.iterator().next().getClass();
            try {
                return getDao(entityClass).delete(entities);
            } catch (Exception e) {
                throw new DatabaseException(String.format("Deleting of entities '%s' has failed.", entityClass.getName()), e);
            }
        }
        return 0;
    }

    public <T> int createTable(Class<T> entityClass) {
        try {
            return TableUtils.createTable(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Creating of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    public <T> int createTableIfNotExists(Class<T> entityClass) {
        try {
            return TableUtils.createTableIfNotExists(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Creating of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    public <T> int clearTable(Class<T> entityClass) {
        try {
            return TableUtils.clearTable(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Clearing of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    public <T> int dropTable(Class<T> entityClass) {
        return dropTable(entityClass, true);
    }

    public <T> int dropTable(Class<T> entityClass, boolean ignoreErrors) {
        try {
            return TableUtils.dropTable(connectionSource, entityClass, ignoreErrors);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Dropping of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    public <T, ID> QueryBuilder<T, ID> queryBuilder(Class<T> entityClass) {
        Dao<T, ID> dao = getDao(entityClass);
        return dao.queryBuilder();
    }

    public <T, ID> UpdateBuilder<T, ID> updateBuilder(Class<T> entityClass) {
        Dao<T, ID> dao = getDao(entityClass);
        return dao.updateBuilder();
    }

    public <T, ID> DeleteBuilder<T, ID> deleteBuilder(Class<T> entityClass) {
        Dao<T, ID> dao = getDao(entityClass);
        return dao.deleteBuilder();
    }

    public <T, ID> Dao<T, ID> getDao(Class<T> entityClass) {
        try {
            return DaoManager.createDao(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Getting of DAO for '%s' has failed.", entityClass.getName()), e);
        }
    }

    public ResultSet executeQuery(String sqlQuery) {
        com.j256.ormlite.support.DatabaseConnection connection = null;
        try {
            try {
                connection = connectionSource.getReadOnlyConnection();
                CompiledStatement compiledStatement = connection.compileStatement(sqlQuery,
                        StatementBuilder.StatementType.SELECT, new FieldType[0],
                        com.j256.ormlite.support.DatabaseConnection.DEFAULT_RESULT_FLAGS);
                return TypeUtils.getFieldValue(compiledStatement.runQuery(null), "resultSet");
            } finally {
                if (connection != null) {
                    connectionSource.releaseConnection(connection);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Executing of '%s' has failed.", sqlQuery), e);
        }
    }

    public int executeUpdate(String sqlQuery) {
        com.j256.ormlite.support.DatabaseConnection connection = null;
        try {
            try {
                connection = connectionSource.getReadWriteConnection();
                return connection.update(sqlQuery, new Object[0], new FieldType[0]);
            } finally {
                if (connection != null) {
                    connectionSource.releaseConnection(connection);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Executing of '%s' has failed.", sqlQuery), e);
        }
    }

    public int executeInsert(String sqlQuery) {
        com.j256.ormlite.support.DatabaseConnection connection = null;
        try {
            try {
                connection = connectionSource.getReadWriteConnection();
                return connection.insert(sqlQuery, new Object[0], new FieldType[0], null);
            } finally {
                if (connection != null) {
                    connectionSource.releaseConnection(connection);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Executing of '%s' has failed.", sqlQuery), e);
        }
    }

    public List<Number> executeInsertWithReturn(String sqlQuery) {
        List<Number> generatedKeys = new ArrayList<>();
        com.j256.ormlite.support.DatabaseConnection connection = null;
        try {
            try {
                connection = connectionSource.getReadWriteConnection();
                connection.insert(sqlQuery, new Object[0], new FieldType[0], generatedKeys::add);
                return generatedKeys;
            } finally {
                if (connection != null) {
                    connectionSource.releaseConnection(connection);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Executing of '%s' has failed.", sqlQuery), e);
        }
    }

    public int executeDelete(String sqlQuery) {
        com.j256.ormlite.support.DatabaseConnection connection = null;
        try {
            try {
                connection = connectionSource.getReadWriteConnection();
                return connection.delete(sqlQuery, new Object[0], new FieldType[0]);
            } finally {
                if (connection != null) {
                    connectionSource.releaseConnection(connection);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Executing of '%s' has failed.", sqlQuery), e);
        }
    }

    public void rollback() {
        throw new RollbackTransactionException();
    }

    public void close() {
        try {
            if (this.connectionSource.isOpen()) {
                this.connectionSource.close();
            }
            this.connectionSource = null;
        } catch (SQLException e) {
            //do nothing
        }
    }

    public <T> T callInTransaction(DatabaseFunction<T> executor) {
        try {
            return TransactionManager.callInTransaction(connectionSource, () -> executor.apply(this));
        } catch (RollbackTransactionException e) {
            return null;
        } catch (Exception e) {
            throw new DatabaseException("Transaction execution has failed.", e);
        }
    }

    public void callInTransaction(DatabaseConsumer executor) {
        try {
            TransactionManager.callInTransaction(connectionSource, () -> {
                executor.accept(this);
                return null;
            });
        } catch (SQLException e) {
            if (e.getCause() instanceof RollbackTransactionException) {
                return;
            }
            throw new DatabaseException("Transaction execution has failed.", e);
        } catch (Exception e) {
            throw new DatabaseException("Transaction execution has failed.", e);
        }
    }
}
