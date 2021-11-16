package eu.ibagroup.easyrpa.openframework.database.connection;

import eu.ibagroup.easyrpa.openframework.database.common.DbSession;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

public class ConnectionKeeper implements AutoCloseable {

    private static DbSession session = null;

    private static ConnectionSource ormConnectionSource = null;

    public static DbSession getSession() {
        return session;
    }

    public static void setSession(DbSession session) throws SQLException {
        ConnectionKeeper.session = session;
    }

    public static ConnectionSource getOrmConnectionSource() {
        return ormConnectionSource;
    }

    public static void setOrmConnectionSource(ConnectionSource ormConnectionSource) {
        ConnectionKeeper.ormConnectionSource = ormConnectionSource;
    }

    public static void closeOrmConnection() throws SQLException {
        if(ormConnectionSource != null){
            if(ormConnectionSource.isOpen()){
                ormConnectionSource.close();
            }
            ormConnectionSource = null;
        }
    }
    public static void closeSessionConnection() throws Exception {
        session.close();
    }

    public static void closeConnections() throws Exception {
        closeOrmConnection();
        closeSessionConnection();
    }

    @Override
    public void close() throws Exception {
        if (ormConnectionSource != null) {
            if (ormConnectionSource.isOpen()) {
                ormConnectionSource.close();
            }
            ormConnectionSource = null;
        }
    }
}
