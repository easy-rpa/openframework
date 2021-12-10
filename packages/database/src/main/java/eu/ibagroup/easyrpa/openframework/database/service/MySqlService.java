package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;
import eu.ibagroup.easyrpa.openframework.database.connection.MySqlConnectionHelper;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbConnector;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MYSQL_SECRET_VAULT;
import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.MYSQL_URL_CONF_FIELD;

public class MySqlService extends DatabaseService {

    @Inject
    public MySqlService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
        this.connectionString = getRpaServices().getConfigParam(MYSQL_URL_CONF_FIELD);
        this.userName = getRpaServices().getCredentials(MYSQL_SECRET_VAULT).getUser();
        this.password = getRpaServices().getCredentials(MYSQL_SECRET_VAULT).getPassword();
    }

    public MySqlService(String connectionString, String userName, String password) {
        super(connectionString, userName, password);
    }

    @Override
    DatabaseService initJdbcConnection() throws SQLException, ClassNotFoundException {
        if (connectionKeeper == null || !ConnectionKeeper.sessionAlive()) {
            MySqlConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbConnector.getConnection().getSession());
        }
        return this;
    }

    @Override
    MySqlService initOrmConnection() throws SQLException {
        if (ConnectionKeeper.getOrmConnectionSource() == null || !ConnectionKeeper.getOrmConnectionSource().isOpen()) {
            ConnectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
