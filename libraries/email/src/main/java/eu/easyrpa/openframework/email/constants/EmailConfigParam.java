package eu.easyrpa.openframework.email.constants;

/**
 * The list of configuration parameter names which can be specified within RPA platform to provide necessary
 * for working with mailbox and email messages information.
 */
public class EmailConfigParam {

    public static final String OUTBOUND_EMAIL_SERVER = "outbound.email.server";

    public static final String OUTBOUND_EMAIL_PROTOCOL = "outbound.email.protocol";

    public static final String OUTBOUND_EMAIL_SECRET = "outbound.email.secret";

    public static final String OUTBOUND_EMAIL_CHANNEL = "outbound.email.channel";

    public static final String INBOUND_EMAIL_SERVER = "inbound.email.server";

    public static final String INBOUND_EMAIL_PROTOCOL = "inbound.email.protocol";

    public static final String INBOUND_EMAIL_SECRET = "inbound.email.secret";

    public static final String MAILBOX_DEFAULT_FOLDER = "mailbox.default.folder";

    public static final String SENDER_NAME_TPL = "%s.sender.name";

    public static final String FROM_TPL = "%s.from";

    public static final String RECIPIENTS_TPL = "%s.recipients";

    public static final String CC_RECIPIENTS_TPL = "%s.cc.recipients";

    public static final String BCC_RECIPIENTS_TPL = "%s.bcc.recipients";

    public static final String REPLY_TO_TPL = "%s.reply.to";

    public static final String SUBJECT_TPL = "%s.subject";

    public static final String TEMPLATE_NAME_TPL = "%s.tpl";

    public static final String CHARSET_TPL = "%s.charset";

    private EmailConfigParam() {
    }
}