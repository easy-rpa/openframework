package eu.ibagroup.easyrpa.openframework.email.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Represents specific email address.
 */
public class EmailAddress {

    /**
     * The string with email address without person name.
     * <br>E.g. {@code user@example.com}
     */
    private String address;

    /**
     * The name of person related to this email address.
     * <br>E.g. {@code John Doe}
     */
    private String personal;

    /**
     * The string with full email address according to RFC822.
     * <br>E.g. {@code "John Doe" <user@example.com>}
     */
    private String rfc822Address;

    /**
     * Constructs a new EmailAddress.
     *
     * @param address  the string with email address.
     * @param personal the name of person related to this email address.
     */
    @JsonCreator
    public EmailAddress(@JsonProperty("address") String address, @JsonProperty("personal") String personal) {
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(address, personal);
            this.address = internetAddress.getAddress();
            this.personal = internetAddress.getPersonal();
            this.rfc822Address = internetAddress.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Constructs a new EmailAddress.
     *
     * @param address the string with email address according to RFC822.
     */
    public EmailAddress(String address) {
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(address);
            this.address = internetAddress.getAddress();
            this.personal = internetAddress.getPersonal();
            this.rfc822Address = internetAddress.toString();
        } catch (AddressException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets email address without person name.
     *
     * @return string with email address without person name.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Gets the name of person related to this email address.
     *
     * @return string with name of person related to this email address.
     */
    public String getPersonal() {
        return this.personal;
    }

    /**
     * Converts this email address into HTML string with mailto link.
     *
     * @return the HTML string with mailto link.
     */
    public String toHtml() {
        if (personal != null) {
            return String.format("<b>%1$s</b> <span>&lt;<a href=\"mailto:%2$s\" target=\"_blank\">%2$s</a>&gt;</span>", personal, address);
        }
        return String.format("<span>&lt;<a href=\"mailto:%1$s\" target=\"_blank\">%1$s</a>&gt;</span>", address);
    }

    @Override
    public String toString() {
        return this.rfc822Address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress)) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}