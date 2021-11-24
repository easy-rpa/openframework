package eu.ibagroup.easyrpa.openframework.database.connection;

import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MYSQL_JDBC_DRIVER;

public class MySqlConnectionHelper {
    public static void initialize(String connectionString, String userName, String password) throws ClassNotFoundException, SQLException {
        Class.forName(MYSQL_JDBC_DRIVER);
        OpenFrameworkDbConnector.initialize(
                connectionString,
                userName,
                password
        );
    }
}
