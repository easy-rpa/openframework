package eu.ibagroup.easyrpa.openframework.email.message;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

public class EmailAddress {

    private String address;

    private String personal;

    private String rfc822Address;

    public EmailAddress() {
    }

    public EmailAddress(String address, String name) {
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(address, name);
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

    public String getRfc822Address() {
        return this.rfc822Address;
    }

    public String toString() {
        return this.rfc822Address;
    }
}