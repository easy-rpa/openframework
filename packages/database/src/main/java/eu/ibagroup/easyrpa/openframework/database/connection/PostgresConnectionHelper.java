package eu.ibagroup.easyrpa.openframework.database.connection;

import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.POSTGRES_JDBC_DRIVER;

public class PostgresConnectionHelper {

    public static void initialize(String connectionString, String userName, String password) throws ClassNotFoundException, SQLException {
        Class.forName(POSTGRES_JDBC_DRIVER);
        OpenFrameworkDbConnector.initialize(
                connectionString,
                userName,
                password
        );
    }
}
