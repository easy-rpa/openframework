package eu.ibagroup.easyrpa.openframework.database;

public class DatabaseParams {

    private String jdbcUrl;

    private String user;

    private String password;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * https://www.baeldung.com/java-jdbc-url-format
     *
     * @param jdbcUrl
     * @return
     */
    public DatabaseParams url(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DatabaseParams user(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DatabaseParams pass(String password) {
        this.password = password;
        return this;
    }
}
