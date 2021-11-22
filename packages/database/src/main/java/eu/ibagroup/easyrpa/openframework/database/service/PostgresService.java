package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbHelper;
import eu.ibagroup.easyrpa.openframework.database.connection.PostgresConnectionHelper;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.POSTGRES_SECRET_VAULT;
import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.POSTGRES_URL_CONF_FIELD;

public class PostgresService extends DatabaseService {

    @Inject
    public PostgresService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
    }

    @Override
    DatabaseService initPureConnection() throws SQLException, ClassNotFoundException {
        if (!ConnectionKeeper.sessionAlive()) {
            String connectionString = getRpaServices().getConfigParam(POSTGRES_URL_CONF_FIELD);

            String userName = getRpaServices().getCredentials(POSTGRES_SECRET_VAULT).getUser();
            String password = getRpaServices().getCredentials(POSTGRES_SECRET_VAULT).getPassword();

            PostgresConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbHelper.getConnection().getSession());
        }
        return this;
    }

    @Override
    DatabaseService initOrmConnection() throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null || !ConnectionKeeper.getOrmConnectionSource().isOpen()) {
            String connectionString = this.rpaServices.getConfigParam(POSTGRES_URL_CONF_FIELD);

            String userName = rpaServices.getCredentials(POSTGRES_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(POSTGRES_SECRET_VAULT).getPassword();

            ConnectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
