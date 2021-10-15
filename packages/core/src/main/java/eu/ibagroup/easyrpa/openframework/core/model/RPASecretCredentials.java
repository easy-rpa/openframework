package eu.ibagroup.easyrpa.openframework.core.model;

import java.util.Objects;

/**
 * Keeps user name and password pair.
 */
public class RPASecretCredentials {
    private String user;

    private String password;

    public RPASecretCredentials() {
    }

    public RPASecretCredentials(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPASecretCredentials that = (RPASecretCredentials) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, password);
    }

    @Override
    public String toString() {
        return "RPASecretCredentials{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
