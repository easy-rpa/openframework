package eu.ibagroup.easyrpa.openframework.database.common;

import eu.ibagroup.easyrpa.openframework.database.utils.SqlUtils;

import java.sql.Timestamp;
import java.util.Date;

public interface SqlQuery<T> {
    String getSQLString();

    Class<T> getTargetEntityClass();

    default String formatValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "'" + SqlUtils.escapeSql(value.toString()) + "'";
        } else if (value instanceof Integer || value instanceof Long) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        } else if (value instanceof Date) {
            Date dateValue = (Date) value;
            Timestamp sqlDateTime = new Timestamp(dateValue.getTime());
            String msSqlDateTimeFormatted = sqlDateTime.toString().replace(" ", "T");
            return "'" + msSqlDateTimeFormatted + "'";
        } else {
            return "'" + SqlUtils.escapeSql(value.toString()) + "'";
        }
    }
}
