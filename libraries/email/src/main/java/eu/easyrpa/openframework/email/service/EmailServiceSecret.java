package eu.easyrpa.openframework.email.service;

import java.util.Objects;

/**
 * Keeps secret information for establishing connection with email server.
 */
public class EmailServiceSecret {
    private String user;

    private String password;

    public EmailServiceSecret() {
    }

    public EmailServiceSecret(String user, String password) {
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
        EmailServiceSecret that = (EmailServiceSecret) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, password);
    }

    @Override
    public String toString() {
        return "EmailServiceSecret{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}