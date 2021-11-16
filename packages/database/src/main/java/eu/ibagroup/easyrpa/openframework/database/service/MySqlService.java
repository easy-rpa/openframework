package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.MySqlConnectionHelper;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbHelper;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.*;

public class MySqlService extends CommonDbService{

    @Inject
    public MySqlService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
    }

    @Override
    public CommonDbService initPureConnection() throws SQLException, ClassNotFoundException {
        if (this.getSession() == null || this.getSession().getConnection() == null || this.getSession().getConnection().isClosed()) {
            String connectionString = getRpaServices().getConfigParam(MYSQL_URL_CONF_FIELD);

            String userName = getRpaServices().getCredentials(MYSQL_SECRET_VAULT).getUser();
            String password = getRpaServices().getCredentials(MYSQL_SECRET_VAULT).getPassword();

            MySqlConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbHelper.getConnection().getSession());
        }
        return this;
    }

    @Override
    public MySqlService initOrmConnection() throws SQLException {
        if (connectionKeeper.getOrmConnectionSource() == null || !connectionKeeper.getOrmConnectionSource().isOpen()) {
            String connectionString = this.rpaServices.getConfigParam(MYSQL_URL_CONF_FIELD);

            String userName = rpaServices.getCredentials(MYSQL_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(MYSQL_SECRET_VAULT).getPassword();

            connectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
