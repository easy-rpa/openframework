package eu.ibagroup.easyrpa.openframework.database.common;

import java.sql.Connection;
import java.sql.SQLException;

public class OpenFrameworkConnection {

    private Connection connection = null;

    public OpenFrameworkConnection(Connection connection) {
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

    public DbSession getSession() throws SQLException {
        return new DbSession(this.connection);
    }
}
