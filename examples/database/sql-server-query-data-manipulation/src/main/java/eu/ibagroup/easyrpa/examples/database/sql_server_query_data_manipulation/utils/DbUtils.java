package eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.utils;

public class DbUtils{
    public static String formatQuery(String query, String... params){
        String outQuery = query;
        for(String prm : params){
            outQuery = outQuery.replaceFirst("\\?", "'"+prm+"'");
        }
        return outQuery;
    }
}
