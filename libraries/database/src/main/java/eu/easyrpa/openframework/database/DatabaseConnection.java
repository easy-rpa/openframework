package eu.easyrpa.openframework.database;

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
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import eu.easyrpa.openframework.core.utils.TypeUtils;
import eu.easyrpa.openframework.database.exceptions.DatabaseException;
import eu.easyrpa.openframework.database.function.DatabaseFunction;
import eu.easyrpa.openframework.database.exceptions.RollbackTransactionException;
import eu.easyrpa.openframework.database.function.DatabaseConsumer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents database connection and provides functionality to work with it.
 */
public class DatabaseConnection implements AutoCloseable {

    /**
     * ORMLite pool of JDBC connections.
     */
    private JdbcPooledConnectionSource connectionSource;

    /**
     * Constructs a new DatabaseConnection using given parameters.
     * <p>
     * This method cannot be called directly. Use {@link DatabaseService#withConnection} or
     * {@link DatabaseService#withTransaction} instead to get instance of this DatabaseConnection.
     *
     * @param dbParams object with parameters necessary to establish database connection.
     */
    DatabaseConnection(DatabaseParams dbParams) {
        try {
            connectionSource = new JdbcPooledConnectionSource(dbParams.getJdbcUrl(), dbParams.getUser(), dbParams.getPassword());
        } catch (Exception e) {
            throw new DatabaseException("JDBC connection initialization has failed.", e);
        }
    }

    /**
     * Selects all records from the table related to given entity class.
     * <p>
     * Relationship between database table and entity is defined using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entityClass class of entity with specified <code>DatabaseTable</code> and <code>DatabaseField</code>
     *                    annotations.
     * @param <T>         type of returning records.
     * @return list of entity records related to given class.
     * @throws DatabaseException in case of some errors.
     */
    public <T> List<T> selectAll(Class<T> entityClass) {
        try {
            return getDao(entityClass).queryForAll();
        } catch (Exception e) {
            throw new DatabaseException(String.format("Selecting of all '%s' entities has failed.", entityClass.getName()), e);
        }
    }

    /**
     * Creates and inserts a new record related to given entity.
     * <p>
     * A database table where record should be inserted is defined by the class of given entity using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entity object representing record that should be inserted into database table.
     * @param <T>    type of given entity.
     * @return amount of inserted records. If <code>entity</code> is <code>null</code> nothing will be inserted and
     * this method returns <code>0</code>.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Creates and inserts new records related to given entities.
     * <p>
     * A database table where records should be inserted is defined by the class of given entities using ORMLite
     * annotations {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entities collection of objects representing records that should be inserted into database table.
     * @param <T>      type of given entities.
     * @return amount of inserted records. If <code>entities</code> is <code>null</code> or empty nothing will be
     * inserted and this method returns <code>0</code>.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Creates and inserts a record related to given entity if no records with the same ID exist in the table yet.
     * <p>
     * A database table where record should be inserted is defined by the class of given entity using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entity object representing record that should be inserted into database table if it's absent.
     * @param <T>    type of given entity.
     * @return either the object representing a new record if it was inserted or the object representing existed
     * record in the database table. If record have been inserted its ID will be populated with actual value.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Creates and inserts records related to given entities if no records with the same IDs exist in the table yet.
     * <p>
     * A database table where records should be inserted is defined by the class of given entities using ORMLite
     * annotations {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entities collection of objects representing records that should be inserted into database table if they
     *                 are absent.
     * @param <T>      type of given entities.
     * @return list of objects representing inserted or existed records in the database table. If records have been
     * inserted their IDs will be populated with actual values.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Creates and inserts a record related to given entity or updates existed record with the same ID.
     * <p>
     * Relating database table is defined by the class of given entity using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entity object representing record that should be inserted or updated in database table.
     * @param <T>    type of given entity.
     * @return status object with info whether an insert or update was performed.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Creates and inserts a records related to given entities or updates existed records with the same IDs.
     * <p>
     * Relating database table is defined by the class of given entities using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entities collection of objects representing records that should be inserted or updated in database table.
     * @param <T>      type of given entities.
     * @return list of status object with info whether an insert or update was performed per each input entity.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Updates existing record related to given entity by ID.
     * <p>
     * A database table where record should be updated is defined by the class of given entity using ORMLite
     * annotations {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entity object representing record that should be updated in database table.
     * @param <T>    type of given entity.
     * @return amount of updated records. If <code>entity</code> is <code>null</code> nothing will be updated and
     * this method returns <code>0</code>.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Updates existing records related to given entities by IDs.
     * <p>
     * A database table where records should be updated is defined by the class of given entities using ORMLite
     * annotations {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entities collection of objects representing records that should be updated in database table.
     * @param <T>      type of given entities.
     * @return amount of updated records. If <code>entities</code> is <code>null</code> or empty nothing will be
     * updated and this method returns <code>0</code>.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Deletes existing record related to given entity by ID.
     * <p>
     * A database table where record should be deleted is defined by the class of given entity using ORMLite
     * annotations {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entity object representing record that should be deleted from database table.
     * @param <T>    type of given entity.
     * @return amount of delete records. If <code>entity</code> is <code>null</code> nothing will be deleted and
     * this method returns <code>0</code>.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Deletes existing records related to given entities by IDs.
     * <p>
     * A database table where records should be deleted is defined by the class of given entities using ORMLite
     * annotations {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entities collection of objects representing records that should be deleted from database table.
     * @param <T>      type of given entities.
     * @return amount of deleted records. If <code>entities</code> is <code>null</code> or empty nothing will be
     * deleted and this method returns <code>0</code>.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Creates a new database table associated with given entity class.
     * <p>
     * Specification of the creating table is defined by given class using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entityClass class of entity to which the database table should be created for.
     * @param <T>         type of entity.
     * @return the number of SQL statements executed to do so.
     * @throws DatabaseException in case of some errors.
     */
    public <T> int createTable(Class<T> entityClass) {
        try {
            return TableUtils.createTable(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Creating of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    /**
     * Creates a new database table associated with given entity class if it does not already exist.
     * This is not supported by all databases.
     * <p>
     * Specification of the creating table is defined by given class using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
     *
     * @param entityClass class of entity to which the database table should be created for.
     * @param <T>         type of entity.
     * @return the number of SQL statements executed to do so.
     * @throws DatabaseException in case of some errors.
     */
    public <T> int createTableIfNotExists(Class<T> entityClass) {
        try {
            return TableUtils.createTableIfNotExists(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Creating of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    /**
     * Clears all data out of the database table related to given entity class.
     * <p>
     * For certain database types and with large sized tables, it may take a long time. In some configurations,
     * it may be faster to drop and re-create the table.
     * <p>
     * <b>WARNING:</b> This is [obviously] very destructive and is unrecoverable.
     *
     * @param entityClass class of entity that defines a database table to clear.
     * @param <T>         type of entity.
     * @return amount of cleared records.
     * @throws DatabaseException in case of some errors.
     */
    public <T> int clearTable(Class<T> entityClass) {
        try {
            return TableUtils.clearTable(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Clearing of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    /**
     * Drops database table associated with given entity class. All possible {@link SQLException} are ignored.
     * <p>
     * <b>WARNING:</b> This is [obviously] very destructive and is unrecoverable.
     *
     * @param entityClass class of entity that defines a database table to drop.
     * @param <T>         type of entity.
     * @return the number of SQL statements executed to do so.
     */
    public <T> int dropTable(Class<T> entityClass) {
        return dropTable(entityClass, true);
    }

    /**
     * Drops database table associated with given entity class.
     * <p>
     * <b>WARNING:</b> This is [obviously] very destructive and is unrecoverable.
     *
     * @param entityClass  class of entity that defines a database table to drop.
     * @param ignoreErrors defines whether all possible {@link SQLException} should be ignored.
     * @param <T>          type of entity.
     * @return the number of SQL statements executed to do so.
     * @throws DatabaseException in case of some errors.
     */
    public <T> int dropTable(Class<T> entityClass, boolean ignoreErrors) {
        try {
            return TableUtils.dropTable(connectionSource, entityClass, ignoreErrors);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Dropping of table for '%s' has failed.", entityClass.getName()), e);
        }
    }

    /**
     * Creates a new query builder object which allows to build a custom SELECT statement.
     * <p>
     * Once query is ready call the method {@link QueryBuilder#query()} to perform it.
     * <p>
     * A target database table is defined by given entity class using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#QueryBuilder-Basics">
     * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#QueryBuilder-Basics</a>
     *
     * @param entityClass class of entity related to database table to which the query is performed for.
     * @param <T>         type of entity that defines a target database table.
     * @param <ID>        type of the entity ID column.
     * @return a new query builder object.
     */
    public <T, ID> QueryBuilder<T, ID> queryBuilder(Class<T> entityClass) {
        Dao<T, ID> dao = getDao(entityClass);
        return dao.queryBuilder();
    }

    /**
     * Creates a new update query builder object which allows to build a custom UPDATE statement.
     * <p>
     * Once query is ready call the method {@link UpdateBuilder#update()} to perform it.
     * <p>
     * A target database table is defined by given entity class using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Building-Statements">
     * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Building-Statements</a>
     *
     * @param entityClass class of entity related to database table to which the update query is performed for.
     * @param <T>         type of entity that defines a target database table.
     * @param <ID>        type of the entity ID column.
     * @return a new update query builder object.
     */
    public <T, ID> UpdateBuilder<T, ID> updateBuilder(Class<T> entityClass) {
        Dao<T, ID> dao = getDao(entityClass);
        return dao.updateBuilder();
    }

    /**
     * Creates a new delete query builder object which allows to build a custom DELETE statement.
     * <p>
     * Once query is ready call the method {@link DeleteBuilder#delete()} to perform it.
     * <p>
     * A target database table is defined by given entity class using ORMLite annotations
     * {@link com.j256.ormlite.table.DatabaseTable} and {@link com.j256.ormlite.field.DatabaseField}.
     * <p>
     * For more info see <a href="https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Building-Statements">
     * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Building-Statements</a>
     *
     * @param entityClass class of entity related to database table to which the delete query is performed for.
     * @param <T>         type of entity that defines a target database table.
     * @param <ID>        type of the entity ID column.
     * @return a new delete query builder object.
     */
    public <T, ID> DeleteBuilder<T, ID> deleteBuilder(Class<T> entityClass) {
        Dao<T, ID> dao = getDao(entityClass);
        return dao.deleteBuilder();
    }

    /**
     * Gets ORMLite DAO object that provides functionality of reading and writing data from the database.
     *
     * @param entityClass class of entity to which the DAO object should be returned for.
     * @param <T>         type of entity.
     * @param <ID>        type of the entity ID column.
     * @return ORMLite DAO object.
     * @throws DatabaseException in case of some errors.
     */
    public <T, ID> Dao<T, ID> getDao(Class<T> entityClass) {
        try {
            return DaoManager.createDao(connectionSource, entityClass);
        } catch (Exception e) {
            throw new DatabaseException(String.format("Getting of DAO for '%s' has failed.", entityClass.getName()), e);
        }
    }

    /**
     * Executes raw SQL query with returning of results.
     *
     * @param sqlQuery SQL query string to execute.
     * @return result set object with results of query execution.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Executes raw UPDATE SQL statement.
     *
     * @param sqlQuery UPDATE SQL string to execute.
     * @return amount of updated records.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Executes raw INSERT SQL statement.
     *
     * @param sqlQuery INSERT SQL string to execute.
     * @return amount of inserted records.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Executes raw INSERT SQL statement with returning of IDs of inserted records.
     *
     * @param sqlQuery INSERT SQL string to execute.
     * @return list with IDs of inserted records.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Executes raw DELETE SQL statement.
     *
     * @param sqlQuery DELETE SQL string to execute.
     * @return amount of deleted records.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Performs given actions with returning of result within single transaction.
     * <p>
     * The transaction is automatically committed in the end of execution or rolled back in case of some errors.
     * If necessary it can be rolled back immediately using {@link #rollback()} method.
     *
     * @param executor lambda expression or instance of {@link DatabaseFunction} that defines actions need
     *                 to be performed within transaction.
     * @param <T>      type of computed result that is returned by <code>executor</code>.
     * @return computed by <code>executor</code> result.
     * @throws DatabaseException in case of some errors.
     */
    public <T> T callInTransaction(DatabaseFunction<T> executor) {
        try {
            return TransactionManager.callInTransaction(connectionSource, () -> executor.apply(this));
        } catch (RollbackTransactionException e) {
            return null;
        } catch (Exception e) {
            throw new DatabaseException("Transaction execution has failed.", e);
        }
    }

    /**
     * Performs given actions within single transaction.
     * <p>
     * The transaction is automatically committed in the end of execution or rolled back in case of some errors.
     * If necessary it can be rolled back immediately using {@link #rollback()} method.
     *
     * @param executor lambda expression or instance of {@link DatabaseConsumer} that defines actions need
     *                 to be performed within transaction.
     * @throws DatabaseException in case of some errors.
     */
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

    /**
     * Throws special {@link RollbackTransactionException} that initiate rollback of the current transactions.
     */
    public void rollback() {
        throw new RollbackTransactionException();
    }

    /**
     * Returns the current ORMLite connection source for some specific purpose.
     *
     * @return instance of used ORMLite connection source.
     */
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    /**
     * Closes the underlying ORMLite connection source without throwing of errors.
     */
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
}
