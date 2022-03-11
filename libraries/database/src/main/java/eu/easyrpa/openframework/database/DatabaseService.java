package eu.easyrpa.openframework.database;

import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.database.exceptions.DatabaseException;
import eu.easyrpa.openframework.database.function.DatabaseFunction;
import eu.easyrpa.openframework.database.function.DatabaseConsumer;

import javax.inject.Inject;

/**
 * Service that provides convenient functionality for working with remote databases within RPA processes.
 * <p>
 * Actually this service provides functionality of ORMLite library with more easier way of its initialization and
 * establishing of database connection.
 * <p>
 * For more info see <a href="https://ormlite.com">https://ormlite.com</a>
 */
public class DatabaseService {

    /**
     * Instance of RPA services accessor that allows to get secret vault entries with parameters necessary
     * for establishing of database connection.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Default constructor of this DatabaseService.
     * <p>
     * This constructor should be used in case of manual providing of <code>DatabaseParams</code> to establish
     * database connection. E.g.:
     * <pre>
     *  DatabaseParams dbParams = new DatabaseParams().url("jdbc_url").user("dbuser").pass("password");
     *  new DatabaseService().withConnection(dbParams, (c)->{
     *   ...
     *  });
     * </pre>
     */
    public DatabaseService() {
    }

    /**
     * Constructs DatabaseService with provided <code>RPAServicesAccessor</code>.
     * <p>
     * This constructor is used in case of injecting of this DatabaseService using <code>@Inject</code> annotation.
     * E.g.:
     * <pre>
     * {@code @Inject}
     *  private DatabaseService dbService;
     *
     *  public void execute() {
     *   dbService.withConnection("testdb", (c) -> {
     *     ...
     *    });
     *  }
     * </pre>
     *
     * @param rpaServices instance of accessor that allows to use use provided by RPA platform services
     *                    like configuration, secret vault etc.
     * @see Inject
     * @see RPAServicesAccessor
     */
    @Inject
    public DatabaseService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    /**
     * Establishes connection to specific database and performs given actions with returning of result.
     * <p>
     * This method does the following. Establishes database connection, performs given actions using this connection
     * and close it in the end or in case of errors. The result of actions is returned to the caller.
     * <p>
     * Necessary for establishing connection parameters are looked up in secret vault of RPA platform with
     * key specified in <code>databaseAlias</code>. The value in secret vault should be in the following
     * JSON format:
     * <pre>
     *     {
     *          "jdbcUrl":"jdbc_url",
     *          "user": "db_user",
     *          "password": "db_user_password"
     *     }
     * </pre>
     *
     * @param databaseAlias the key of secret vault entry that keeps necessary for establishing database connection
     *                      parameters in JSON format.
     * @param executor      lambda expression or instance of <code>DatabaseFunction</code> that defines actions need
     *                      to be performed using database connection.
     * @param <T>           type of computed result that is returned by <code>executor</code>.
     * @return computed by <code>executor</code> result.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseFunction
     */
    public <T> T withConnection(String databaseAlias, DatabaseFunction<T> executor) {
        return withConnection(getForAlias(databaseAlias), executor);
    }

    /**
     * Establishes connection to specific database and performs given actions with returning of result.
     * <p>
     * This method does the following. Establishes database connection, performs given actions using this connection
     * and close it in the end or in case of errors. The result of actions is returned to the caller.
     *
     * @param params   necessary for establishing database connection parameters.
     * @param executor lambda expression or instance of <code>DatabaseFunction</code> that defines actions need
     *                 to be performed using database connection.
     * @param <T>      type of computed result that is returned by <code>executor</code>.
     * @return computed by <code>executor</code> result.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseFunction
     */
    public <T> T withConnection(DatabaseParams params, DatabaseFunction<T> executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            return (T) executor.apply(con);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("An error occurred during working with database.", e);
        }
    }

    /**
     * Establishes connection to specific database and performs given actions.
     * <p>
     * This method does the following. Establishes database connection, performs given actions using this connection
     * and close it in the end or in case of errors.
     * <p>
     * Necessary for establishing connection parameters are looked up in secret vault of RPA platform with
     * key specified in <code>databaseAlias</code>. The value in secret vault should be in the following
     * JSON format:
     * <pre>
     *     {
     *          "jdbcUrl":"jdbc_url",
     *          "user": "db_user",
     *          "password": "db_user_password"
     *     }
     * </pre>
     *
     * @param databaseAlias the key of secret vault entry that keeps necessary for establishing database connection
     *                      parameters in JSON format.
     * @param executor      lambda expression or instance of <code>DatabaseConsumer</code> that defines actions need
     *                      to be performed using database connection.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseConsumer
     */
    public void withConnection(String databaseAlias, DatabaseConsumer executor) {
        withConnection(getForAlias(databaseAlias), executor);
    }

    /**
     * Establishes connection to specific database and performs given actions.
     * <p>
     * This method does the following. Establishes database connection, performs given actions using this connection
     * and close it in the end or in case of errors.
     *
     * @param params   necessary for establishing database connection parameters.
     * @param executor lambda expression or instance of <code>DatabaseConsumer</code> that defines actions need
     *                 to be performed using database connection.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseConsumer
     */
    public void withConnection(DatabaseParams params, DatabaseConsumer executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            executor.accept(con);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("An error occurred during working with database.", e);
        }
    }

    /**
     * Establishes connection to specific database and performs given actions with returning of result within
     * single transaction.
     * <p>
     * This method does the following. Establishes database connection and begins transaction, performs given actions
     * using established connection, commits transaction and closes connection. In case of errors the transaction is
     * rolled back and connection is closed. If necessary the transaction can be rolled back immediately using
     * {@link DatabaseConnection#rollback()} method.
     * <p>
     * Necessary for establishing connection parameters are looked up in secret vault of RPA platform with
     * key specified in <code>databaseAlias</code>. The value in secret vault should be in the following
     * JSON format:
     * <pre>
     *     {
     *          "jdbcUrl":"jdbc_url",
     *          "user": "db_user",
     *          "password": "db_user_password"
     *     }
     * </pre>
     *
     * @param databaseAlias the key of secret vault entry that keeps necessary for establishing database connection
     *                      parameters in JSON format.
     * @param executor      lambda expression or instance of <code>DatabaseFunction</code> that defines actions need
     *                      to be performed using database connection within transaction.
     * @param <T>           type of computed result that is returned by <code>executor</code>.
     * @return computed by <code>executor</code> result.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseFunction
     */
    public <T> T withTransaction(String databaseAlias, DatabaseFunction<T> executor) {
        return withTransaction(getForAlias(databaseAlias), executor);
    }

    /**
     * Establishes connection to specific database and performs given actions with returning of result within
     * single transaction.
     * <p>
     * This method does the following. Establishes database connection and begins transaction, performs given actions
     * using established connection, commits transaction and closes connection. In case of errors the transaction is
     * rolled back and connection is closed. If necessary the transaction can be rolled back immediately using
     * {@link DatabaseConnection#rollback()} method.
     *
     * @param params   necessary for establishing database connection parameters.
     * @param executor lambda expression or instance of <code>DatabaseFunction</code> that defines actions need
     *                 to be performed using database connection within transaction.
     * @param <T>      type of computed result that is returned by <code>executor</code>.
     * @return computed by <code>executor</code> result.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseFunction
     */
    public <T> T withTransaction(DatabaseParams params, DatabaseFunction<T> executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            return con.callInTransaction(executor);
        }
    }

    /**
     * Establishes connection to specific database and performs given actions within single transaction.
     * <p>
     * This method does the following. Establishes database connection and begins transaction, performs given actions
     * using established connection, commits transaction and closes connection. In case of errors the transaction is
     * rolled back and connection is closed. If necessary the transaction can be rolled back immediately using
     * {@link DatabaseConnection#rollback()} method.
     * <p>
     * Necessary for establishing connection parameters are looked up in secret vault of RPA platform with
     * key specified in <code>databaseAlias</code>. The value in secret vault should be in the following
     * JSON format:
     * <pre>
     *     {
     *          "jdbcUrl":"jdbc_url",
     *          "user": "db_user",
     *          "password": "db_user_password"
     *     }
     * </pre>
     *
     * @param databaseAlias the key of secret vault entry that keeps necessary for establishing database connection
     *                      parameters in JSON format.
     * @param executor      lambda expression or instance of <code>DatabaseConsumer</code> that defines actions need
     *                      to be performed using database connection within transaction.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseConsumer
     */
    public void withTransaction(String databaseAlias, DatabaseConsumer executor) {
        withTransaction(getForAlias(databaseAlias), executor);
    }

    /**
     * Establishes connection to specific database and performs given actions within single transaction.
     * <p>
     * This method does the following. Establishes database connection and begins transaction, performs given actions
     * using established connection, commits transaction and closes connection. In case of errors the transaction is
     * rolled back and connection is closed. If necessary the transaction can be rolled back immediately using
     * {@link DatabaseConnection#rollback()} method.
     *
     * @param params   necessary for establishing database connection parameters.
     * @param executor lambda expression or instance of <code>DatabaseFunction</code> that defines actions need
     *                 to be performed using database connection within transaction.
     * @throws DatabaseException in case of some errors.
     * @see DatabaseConsumer
     */
    public void withTransaction(DatabaseParams params, DatabaseConsumer executor) {
        try (DatabaseConnection con = new DatabaseConnection(params)) {
            con.callInTransaction(executor);
        }
    }

    /**
     * Looks up database connection parameters in secret vault of RPA platform by given key.
     * <p>
     * The value in secret vault is expected to be in the following JSON format:
     * <pre>
     *     {
     *          "jdbcUrl":"jdbc_url",
     *          "user": "db_user",
     *          "password": "db_user_password"
     *     }
     * </pre>
     *
     * @param databaseAlias the key of secret vault entry that keeps necessary parameters in JSON format.
     * @return instance of {@link DatabaseParams} with found and parsed database connection parameters.
     * @throws IllegalStateException    if this instance of DatabaseService is created using default constructor.
     * @throws IllegalArgumentException if no secret vault entries is found with given key.
     */
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
