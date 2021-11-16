package eu.ibagroup.easyrpa.openframework.database.service;

import com.j256.ormlite.support.ConnectionSource;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.database.common.DbSession;
import eu.ibagroup.easyrpa.openframework.database.connection.ConnectionKeeper;

import java.sql.SQLException;

public abstract class CommonDbService {
    ConnectionKeeper connectionKeeper = null;

    RPAServicesAccessor rpaServices;

    public abstract CommonDbService initPureConnection() throws SQLException, ClassNotFoundException;
    public abstract CommonDbService initOrmConnection() throws SQLException, ClassNotFoundException;

    public CommonDbService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    public DbSession getSession() {
        return connectionKeeper.getSession();
    }

    public void setSession(DbSession session) throws SQLException {
        connectionKeeper.setSession(session);
    }

    public ConnectionSource getOrmConnectionSource() {
        if(connectionKeeper.getOrmConnectionSource() == null){
            throw new RuntimeException("ORM Lite connection must be initialized first");
        }
        return connectionKeeper.getOrmConnectionSource();
    }


    RPAServicesAccessor getRpaServices() {
        return rpaServices;
    }
}
