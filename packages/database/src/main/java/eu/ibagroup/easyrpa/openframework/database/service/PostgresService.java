package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbConnector;
import eu.ibagroup.easyrpa.openframework.database.connection.PostgresConnectionHelper;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.POSTGRES_SECRET_VAULT;
import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.POSTGRES_URL_CONF_FIELD;

public class PostgresService extends DatabaseService {

    @Inject
    public PostgresService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
        this.connectionString = getRpaServices().getConfigParam(POSTGRES_URL_CONF_FIELD);
        this.userName = getRpaServices().getCredentials(POSTGRES_SECRET_VAULT).getUser();
        this.password = getRpaServices().getCredentials(POSTGRES_SECRET_VAULT).getPassword();
    }

    public PostgresService(String connectionString, String userName, String password) {
        super(connectionString, userName, password);
    }

    @Override
    DatabaseService initJdbcConnection() throws SQLException, ClassNotFoundException {
        if (!ConnectionKeeper.sessionAlive()) {
            PostgresConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbConnector.getConnection().getSession());
        }
        return this;
    }

    @Override
    DatabaseService initOrmConnection() throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null || !ConnectionKeeper.getOrmConnectionSource().isOpen()) {
            ConnectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
