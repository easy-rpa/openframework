package eu.ibagroup.easyrpa.openframework.email.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class EmailAddress {

    private String address;

    private String personal;

    private String rfc822Address;

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

    public String getAddress() {
        return this.address;
    }

    public String getPersonal() {
        return this.personal;
    }

    public String toString() {
        return this.rfc822Address;
    }

    public String toHtml() {
        if (personal != null) {
            return String.format("<b>%1$s</b> <span>&lt;<a href=\"mailto:%2$s\" target=\"_blank\">%2$s</a>&gt;</span>", personal, address);
        }
        return String.format("<span>&lt;<a href=\"mailto:%1$s\" target=\"_blank\">%1$s</a>&gt;</span>", address);
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