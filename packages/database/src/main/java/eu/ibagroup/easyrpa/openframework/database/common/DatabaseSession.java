package eu.ibagroup.easyrpa.openframework.database.common;

import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSession {
    private Connection connection;
    private Statement statement;

    public DatabaseSession(Connection connection) throws SQLException {
        this.connection = connection;
//        this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                ResultSet.CONCUR_UPDATABLE);
        this.statement = connection.createStatement();
    }

    public void close() throws Exception {
        if (!this.statement.isClosed()) {
            this.statement.close();
            this.statement = null;
        }
        closeConnectionHelpers();
    }

    private void closeConnectionHelpers() throws SQLException {
        OpenFrameworkDbConnector.close();
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
