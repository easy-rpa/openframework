package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbHelper;
import eu.ibagroup.easyrpa.openframework.database.connection.PostgresConnectionHelper;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.*;

public class PostgresService extends CommonDbService{

    @Inject
    public PostgresService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
    }

    @Override
    public CommonDbService initPureConnection() throws SQLException, ClassNotFoundException {
        if (this.getSession() == null || this.getSession().getConnection() == null || this.getSession().getConnection().isClosed()) {
            String connectionString = getRpaServices().getConfigParam(POSTGRES_URL_CONF_FIELD);

            String userName = getRpaServices().getCredentials(POSTGRES_SECRET_VAULT).getUser();
            String password = getRpaServices().getCredentials(POSTGRES_SECRET_VAULT).getPassword();

            PostgresConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbHelper.getConnection().getSession());
        }
        return this;
    }

    @Override
    public PostgresService initOrmConnection() throws SQLException {
        if (connectionKeeper.getOrmConnectionSource() == null || !connectionKeeper.getOrmConnectionSource().isOpen()) {
            String connectionString = this.rpaServices.getConfigParam(POSTGRES_URL_CONF_FIELD);

            String userName = rpaServices.getCredentials(POSTGRES_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(POSTGRES_SECRET_VAULT).getPassword();

            connectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
