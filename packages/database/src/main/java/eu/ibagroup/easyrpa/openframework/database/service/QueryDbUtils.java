package eu.ibagroup.easyrpa.openframework.database.service;

import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryDbUtils {
    ConnectionKeeper connectionKeeper;

    QueryDbUtils(ConnectionKeeper connectionKeeper) {
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

    void beginTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = ConnectionKeeper.getSession().getConnection();
        connection.setAutoCommit(false);
    }

    void commitTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = ConnectionKeeper.getSession().getConnection();
        connection.commit();
        connection.setAutoCommit(true);
    }

    void rollbackTransaction() throws SQLException, ClassNotFoundException {
        Connection connection = ConnectionKeeper.getSession().getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }

    void closeConnection() throws Exception {
        ConnectionKeeper.closeSessionConnection();
    }
}
