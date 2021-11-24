package eu.ibagroup.easyrpa.openframework.database.connection;

import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MSSQL_JDBC_DRIVER;

public class SQLServerConnectionHelper {

    public static void initialize(String connectionString, String userName, String password) throws ClassNotFoundException, SQLException {
        Class.forName(MSSQL_JDBC_DRIVER);
        OpenFrameworkDbConnector.initialize(
                connectionString +
                        "encrypt=true;" +
                        "trustServerCertificate=true;" +
                        "loginTimeout=30;",
                userName,
                password
        );
    }
}

