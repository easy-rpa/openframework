package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbConnector;
import eu.ibagroup.easyrpa.openframework.database.connection.SQLServerConnectionHelper;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MSSQL_SECRET_VAULT;
import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MSSQL_URL_CONF_FIELD;

public class SQLServerService extends DatabaseService {

    @Inject
    public SQLServerService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
        this.connectionString = getRpaServices().getConfigParam(MSSQL_URL_CONF_FIELD);
        this.userName = getRpaServices().getCredentials(MSSQL_SECRET_VAULT).getUser();
        this.password = getRpaServices().getCredentials(MSSQL_SECRET_VAULT).getPassword();
    }

    public SQLServerService(String connectionString, String userName, String password) {
        super(connectionString, userName, password);
    }

    @Override
    SQLServerService initJdbcConnection() throws SQLException, ClassNotFoundException {
        if (connectionKeeper == null || !ConnectionKeeper.sessionAlive()) {
            SQLServerConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbConnector.getConnection().getSession());
        }
        return this;
    }

    @Override
    SQLServerService initOrmConnection() throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null || !ConnectionKeeper.getOrmConnectionSource().isOpen()) {
            ConnectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
