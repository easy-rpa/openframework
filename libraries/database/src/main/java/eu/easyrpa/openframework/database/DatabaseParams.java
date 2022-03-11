package eu.easyrpa.openframework.database;

/**
 * Keeps parameters necessary to establish specific database connection.
 */
public class DatabaseParams {

    /**
     * JDBC URL used for connection.
     */
    private String jdbcUrl;

    /**
     * Database user used to perform authentication during connection.
     */
    private String user;

    /**
     * Database user password used to perform authentication during connection.
     */
    private String password;

    /**
     * Gets value of JDBC URL parameter.
     *
     * @return JDBC URL string.
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * Sets value of JDBC URL parameter.
     * <p>
     * For more info regarding JDBC URL formats for different type of databases see
     * <a href="https://www.baeldung.com/java-jdbc-url-format">https://www.baeldung.com/java-jdbc-url-format</a>
     *
     * @param jdbcUrl JDBC URL string to set.
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Sets value of JDBC URL parameter.
     * <p>
     * For more info regarding JDBC URL formats for different type of databases see
     * <a href="https://www.baeldung.com/java-jdbc-url-format">https://www.baeldung.com/java-jdbc-url-format</a>
     *
     * @param jdbcUrl JDBC URL string to set.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public DatabaseParams url(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    /**
     * Gets value of database user parameter.
     * <p>
     * Database user parameter used to perform authentication during connection.
     *
     * @return value of database user parameter.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets value of database user parameter.
     * <p>
     * Database user parameter used to perform authentication during connection.
     *
     * @param user database user name to set.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Sets value of database user parameter.
     * <p>
     * Database user parameter used to perform authentication during connection.
     *
     * @param user database user name to set.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public DatabaseParams user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Gets value of database user password.
     * <p>
     * Database user password used to perform authentication during connection.
     *
     * @return value of database user password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets value of database user password.
     * <p>
     * Database user password used to perform authentication during connection.
     *
     * @param password database user password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets value of database user password.
     * <p>
     * Database user password used to perform authentication during connection.
     *
     * @param password database user password to set.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public DatabaseParams pass(String password) {
        this.password = password;
        return this;
    }
}
