package eu.ibagroup.easyrpa.openframework.database.connection;

import eu.ibagroup.easyrpa.openframework.database.common.OpenFrameworkConnection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class OpenFrameworkDbHelper {

    private static OpenFrameworkConnection connection = null;

    public static void initialize(String connectionString, String userName, String password) throws SQLException {
        if (connection == null) {
            connection = new OpenFrameworkConnection(DriverManager.getConnection(connectionString, userName, password));
        }
    }

    public static void close() {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    public static OpenFrameworkConnection getConnection() {
        if (connection != null) {
            return connection;
        } else {
            throw new RuntimeException("Database service has not been initialized yet");
        }
    }
}