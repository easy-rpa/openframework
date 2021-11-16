package eu.ibagroup.easyrpa.openframework.database.utils;

import org.apache.commons.lang3.StringUtils;

public class SqlUtils {
    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }
}
