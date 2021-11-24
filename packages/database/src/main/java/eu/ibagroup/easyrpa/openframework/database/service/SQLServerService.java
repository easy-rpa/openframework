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
    }

    @Override
    SQLServerService initJdbcConnection() throws SQLException, ClassNotFoundException {
        if (connectionKeeper == null || !ConnectionKeeper.sessionAlive()) {
            String connectionString = this.rpaServices.getConfigParam(MSSQL_URL_CONF_FIELD);
            String userName = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getPassword();

            SQLServerConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbConnector.getConnection().getSession());
        }
        return this;
    }

    @Override
    SQLServerService initOrmConnection() throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null || !ConnectionKeeper.getOrmConnectionSource().isOpen()) {
            String connectionString = this.rpaServices.getConfigParam(MSSQL_URL_CONF_FIELD);

            String userName = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getPassword();

            ConnectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
