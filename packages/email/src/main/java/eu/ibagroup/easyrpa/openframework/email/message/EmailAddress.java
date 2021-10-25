package eu.ibagroup.easyrpa.openframework.email.message;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class EmailAddress implements Serializable {
    private static final long serialVersionUID = 5073437689836759727L;

    private final String address;

    private final String personal;

    private final String rfc822Address;

    private EmailAddress(InternetAddress address) {
        this.address = address.getAddress();
        this.personal = address.getPersonal();
        this.rfc822Address = address.toString();
    }

    public static EmailAddress of(String address, String name) {
        InternetAddress internetAdress;
        try {
            internetAdress = new InternetAddress(address, name);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

        return new EmailAddress(internetAdress);
    }

    public static EmailAddress of(String address) {
        InternetAddress internetAdress;
        try {
            internetAdress = new InternetAddress(address);
        } catch (AddressException e) {
            throw new IllegalArgumentException(e);
        }

        return new EmailAddress(internetAdress);
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