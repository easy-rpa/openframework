package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.connection.MySqlConnectionHelper;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbHelper;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MYSQL_SECRET_VAULT;
import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MYSQL_URL_CONF_FIELD;

public class MySqlService extends DatabaseService {

    @Inject
    public MySqlService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
    }

    @Override
    DatabaseService initPureConnection() throws SQLException, ClassNotFoundException {
        if (connectionKeeper == null || !ConnectionKeeper.sessionAlive()) {
            String connectionString = getRpaServices().getConfigParam(MYSQL_URL_CONF_FIELD);

            String userName = getRpaServices().getCredentials(MYSQL_SECRET_VAULT).getUser();
            String password = getRpaServices().getCredentials(MYSQL_SECRET_VAULT).getPassword();

            MySqlConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbHelper.getConnection().getSession());
        }
        return this;
    }

    @Override
    MySqlService initOrmConnection() throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null || !ConnectionKeeper.getOrmConnectionSource().isOpen()) {
            String connectionString = this.rpaServices.getConfigParam(MYSQL_URL_CONF_FIELD);

            String userName = rpaServices.getCredentials(MYSQL_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(MYSQL_SECRET_VAULT).getPassword();
            ConnectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
