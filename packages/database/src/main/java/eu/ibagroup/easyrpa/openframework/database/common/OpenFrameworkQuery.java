package eu.ibagroup.easyrpa.openframework.database.common;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OpenFrameworkQuery<T> {
    private DbSession session;
    private SqlQuery<T> sqlQuery;

    public OpenFrameworkQuery(DbSession session, SqlQuery<T> sqlQuery) {
        this.session = session;
        this.sqlQuery = sqlQuery;
    }
    private ResultSet executeQuery(String query) throws SQLException {
        return session.executeQuery(query);
    }

    private int executeUpdate(String query) throws SQLException {
        return session.executeUpdate(query);
    }

    private ResultSet executeInsert(String query) throws SQLException {
        return session.executeInsert(query);
    }

    public boolean getResult() throws SQLException {
        return this.executeUpdate(sqlQuery.getSQLString()) == 1;
    }

    public long getSingleInsertResultId() throws SQLException {
        ResultSet resultSetGeneratedIds = this.executeInsert(sqlQuery.getSQLString());
        if (resultSetGeneratedIds != null && resultSetGeneratedIds.next()) {
            return resultSetGeneratedIds.getLong(1);
        }
        return -1;
    }
}
