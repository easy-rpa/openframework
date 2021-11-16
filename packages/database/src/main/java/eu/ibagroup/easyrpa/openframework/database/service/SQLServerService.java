package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.connection.OpenFrameworkDbHelper;
import eu.ibagroup.easyrpa.openframework.database.connection.SQLServerConnectionHelper;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.sql.SQLException;

import static eu.ibagroup.easyrpa.openframework.database.constants.Constants.*;

public class SQLServerService extends CommonDbService {

    @Inject
    public SQLServerService(RPAServicesAccessor rpaServices) {
        super(rpaServices);
    }

    @Override
    public SQLServerService initPureConnection() throws SQLException, ClassNotFoundException {
        if (this.getSession() == null || this.getSession().getConnection() == null || this.getSession().getConnection().isClosed()) {
            String connectionString = this.rpaServices.getConfigParam(MSSQL_URL_CONF_FIELD);
            String userName = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getPassword();

            SQLServerConnectionHelper.initialize(connectionString, userName, password);
            this.setSession(OpenFrameworkDbHelper.getConnection().getSession());
        }
        return this;
    }

    @Override
    public SQLServerService initOrmConnection() throws SQLException {
        if (connectionKeeper.getOrmConnectionSource() == null || !connectionKeeper.getOrmConnectionSource().isOpen()) {
            String connectionString = this.rpaServices.getConfigParam(MSSQL_URL_CONF_FIELD);

            String userName = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getUser();
            String password = rpaServices.getCredentials(MSSQL_SECRET_VAULT).getPassword();

            connectionKeeper.setOrmConnectionSource(new JdbcConnectionSource(connectionString, userName, password));
        }
        return this;
    }
}
