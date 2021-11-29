package eu.ibagroup.utils;

public class DbUtils{
    public static String formatQuery(String query, String... params){
        String outQuery = query;
        for(String prm : params){
            outQuery = outQuery.replaceFirst("\\?", "'"+prm+"'");
        }
        return outQuery;
    }
}
