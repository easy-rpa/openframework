package eu.ibagroup.easyrpa.openframework.database.common;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private Connection connection = null;

    public DatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isClosed() {
        try {
            return this.connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void close() {
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseSession getSession() throws SQLException {
        return new DatabaseSession(this.connection);
    }
}
