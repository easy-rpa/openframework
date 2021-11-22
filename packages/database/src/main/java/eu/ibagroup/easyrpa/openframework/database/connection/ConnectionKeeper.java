package eu.ibagroup.easyrpa.openframework.database.connection;

import com.j256.ormlite.support.ConnectionSource;
import eu.ibagroup.easyrpa.openframework.database.common.DatabaseSession;

import java.sql.SQLException;

public class ConnectionKeeper implements AutoCloseable {

    private static DatabaseSession session = null;

    private static ConnectionSource ormConnectionSource = null;

    public static DatabaseSession getSession() {
        return session;
    }

    public static boolean sessionAlive() throws SQLException {
        if (getSession() == null || getSession().getConnection() == null || getSession().getConnection().isClosed()) {
            return false;
        }
        return true;
    }

    public static void setSession(DatabaseSession session) throws SQLException {
        ConnectionKeeper.session = session;
    }

    public static ConnectionSource getOrmConnectionSource() {
        return ormConnectionSource;
    }

    public static void setOrmConnectionSource(ConnectionSource ormConnectionSource) {
        ConnectionKeeper.ormConnectionSource = ormConnectionSource;
    }

    public static void closeOrmConnection() throws SQLException {
        if (ormConnectionSource != null) {
            if (ormConnectionSource.isOpen()) {
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
        closeConnections();
    }
}
