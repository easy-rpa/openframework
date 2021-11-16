package eu.ibagroup.easyrpa.openframework.database.common;

import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DbSession implements AutoCloseable {
    private Connection connection;
    private Statement statement;

    public DbSession(Connection connection) throws SQLException {
        this.connection = connection;
//        this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                ResultSet.CONCUR_UPDATABLE);
        this.statement = connection.createStatement();
    }

    public <T> OpenFrameworkQuery<T> createQuery(SqlQuery<T> sqlQuery) throws SQLException {
        return new OpenFrameworkQuery<T>(this, sqlQuery);
    }

    public ResultSet executeQuery(String sqlQuery) throws SQLException {
        return statement.executeQuery(sqlQuery);
    }

    public int executeUpdate(String sqlQuery) throws SQLException {
        return statement.executeUpdate(sqlQuery);
    }

    public ResultSet executeInsert(String sqlQuery) throws SQLException {
        statement.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);
        return statement.getGeneratedKeys();
    }

    public ResultSet withTransaction(String t, Function<String, ResultSet> executeQuery) {
        ResultSet rs = executeQuery.apply(t);
        return rs;
    }

    /**
     * @param queriesArray
     * @return
     * @throws SQLException
     */
    public int[] executeTransaction(String... queriesArray) throws SQLException {
        return executeTransaction(new ArrayList<>(Arrays.asList(queriesArray)));
    }

    /**
     * Do not call this method with the SELECT query
     *
     * @param upadteQueries
     * @throws SQLException
     */
    public int[] executeTransaction(List<String> upadteQueries) throws SQLException {
        int[] batchResult = new int[0];
        beginTransaction();
        try {
            for (String query : upadteQueries) {
                statement.addBatch(query);
            }
            batchResult = statement.executeBatch();
            commitTransaction();

        } catch (SQLException ex) {
            rollbackTransaction();
            throw ex;
        }
        return batchResult;
    }

    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }

    @Override
    public void close() throws Exception {
        if (!this.statement.isClosed()) {
            this.statement.close();
            this.statement = null;
        }
        closeConnectionHelpers();
    }

    private void closeConnectionHelpers() throws SQLException {
        OpenFrameworkDbHelper.close();
        if (this.connection != null) {
            if (!this.connection.isClosed()) {
                this.connection.close();
            }
            this.connection = null;
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public Connection getConnection() {
        return connection;
    }
}
