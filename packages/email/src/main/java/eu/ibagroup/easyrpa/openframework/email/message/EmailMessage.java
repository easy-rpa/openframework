package eu.ibagroup.easyrpa.openframework.email.message;

import eu.ibagroup.easyrpa.openframework.email.utils.EmailUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EmailMessage implements Serializable {
    private static final long serialVersionUID = -1475213313051327598L;

    private final String id;

    private final EmailAddress from;

    private final List<EmailAddress> to;

    private final List<EmailAddress> cc;

    private final List<EmailAddress> bcc;

    private final List<EmailAddress> replyTo;

    private final String subject;

    private final String text;

    private final String html;

    private final List<EmailAttachment> inlineFiles;

    private final List<EmailAttachment> attachments;

    private final ZonedDateTime date;

    private final String parentFolder;

    private final Set<EmailFlag> flags;

    private String charset;

    protected EmailMessage(String id, EmailAddress from, List<EmailAddress> to, List<EmailAddress> cc, List<EmailAddress> bcc, List<EmailAddress> replyTo, String subject, String text, String html, List<EmailAttachment> inlineFiles, List<EmailAttachment> attachments, ZonedDateTime date,
            String parentFolder, Set<EmailFlag> flags, String charset) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.replyTo = replyTo;
        this.subject = subject;
        this.text = text;
        this.html = html;
        this.inlineFiles = inlineFiles;
        this.attachments = attachments;
        this.date = date;
        this.parentFolder = parentFolder;
        this.flags = flags;
        this.charset = charset;
    }

    public EmailMessage.EmailMessageBuilder createForwardMessage() {
        return EmailMessage.EmailMessageBuilder.forwardMessage(this);
    }

    public EmailMessage.EmailMessageBuilder createReplyMessage() {
        return EmailMessage.EmailMessageBuilder.replyMessage(this);
    }

    public EmailMessage.EmailMessageBuilder createReplyAllMessage() {
        return EmailMessage.EmailMessageBuilder.replyAllMessage(this);
    }

    public Optional<String> getId() {
        return Optional.ofNullable(this.id);
    }

    public EmailAddress getFrom() {
        return this.from;
    }

    public List<EmailAddress> getTo() {
        return this.to;
    }

    public List<EmailAddress> getCc() {
        return this.cc;
    }

    public List<EmailAddress> getBcc() {
        return this.bcc;
    }

    public List<EmailAddress> getReplyTo() {
        return this.replyTo;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getText() {
        return this.text;
    }

    public Optional<String> getHtml() {
        return Optional.ofNullable(this.html);
    }

    public List<EmailAttachment> getInlineFiles() {
        return this.inlineFiles;
    }

    public Optional<ZonedDateTime> getDate() {
        return Optional.ofNullable(this.date);
    }

    public List<EmailAttachment> getAttachments() {
        return this.attachments;
    }

    public boolean hasInlineAttachments() {
        return !this.inlineFiles.isEmpty();
    }

    public boolean hasAttachments() {
        return !this.attachments.isEmpty();
    }

    public Optional<String> getParentFolder() {
        return Optional.ofNullable(this.parentFolder);
    }

    public boolean isUnread() {
        return this.flags.contains(EmailFlag.UNREAD);
    }

    public Set<EmailFlag> getFlags() {
        return this.flags;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EmailMessage [id=");
        builder.append(this.id != null ? this.id : "n/a");
        builder.append(", parentFolder=");
        builder.append(this.parentFolder != null ? this.parentFolder : "n/a");
        builder.append(", date=");
        builder.append(this.date != null ? this.date : "n/a");
        builder.append(", subject=");
        builder.append(this.subject);
        builder.append(", from=");
        builder.append(this.from);
        builder.append(", replyTo=");
        builder.append(this.replyTo);
        builder.append(", to=");
        builder.append(this.to);
        builder.append(", cc=");
        builder.append(this.cc);
        builder.append(", bcc=");
        builder.append(this.bcc);
        builder.append(", text=");
        builder.append(this.text);
        builder.append(", html=");
        builder.append(this.html);
        builder.append(", inlineFiles=");
        builder.append(this.inlineFiles);
        builder.append(", attachments=");
        builder.append(this.attachments);
        builder.append(", flags=");
        builder.append(this.flags);
        builder.append(", charset=");
        builder.append(this.charset);
        builder.append(']');
        return builder.toString();
    }

    public static class EmailMessageBuilder {
        private String id = null;

        private EmailAddress from;

        private List<EmailAddress> to;

        private List<EmailAddress> cc;

        private List<EmailAddress> bcc;

        private List<EmailAddress> replyTo;

        private String subject;

        private String text;

        private String html;

        private List<EmailAttachment> inlineFiles;

        private List<EmailAttachment> attachments;

        private ZonedDateTime date;

        private String parentFolder;

        private Set<EmailFlag> flags;

        private String charset;

        private EmailMessageBuilder(EmailAddress from, List<EmailAddress> to) {
            this.from = from;
            this.to = to;
            this.cc = new ArrayList<>();
            this.bcc = new ArrayList<>();
            this.replyTo = new ArrayList<>();
            this.subject = "";
            this.text = null;
            this.html = null;
            this.inlineFiles = new ArrayList<>();
            this.attachments = new ArrayList<>();
            this.date = null;
            this.parentFolder = null;
            this.flags = new HashSet<>();
            this.charset = null;
        }

        public static EmailMessage.EmailMessageBuilder newMessage(EmailAddress from, EmailAddress... to) {
            return new EmailMessage.EmailMessageBuilder(from, new ArrayList<>(Arrays.asList(to)));
        }

        public static EmailMessage.EmailMessageBuilder newMessage(EmailAddress from, List<EmailAddress> to) {
            return new EmailMessage.EmailMessageBuilder(from, to);
        }

        public static EmailMessage.EmailMessageBuilder copy(EmailMessage m) {
            EmailMessage.EmailMessageBuilder b = new EmailMessage.EmailMessageBuilder(m.getFrom(), m.getTo());
            b.id = (String) m.getId().orElse(String.valueOf((Object) null));
            b.cc = m.getCc();
            b.bcc = m.getBcc();
            b.replyTo = m.getReplyTo();
            b.subject = m.getSubject();
            b.text = m.getText();
            b.html = (String) m.getHtml().orElse(String.valueOf((Object) null));
            b.inlineFiles = m.getInlineFiles();
            b.attachments = m.getAttachments();
            b.date = (ZonedDateTime) m.getDate().orElse((ZonedDateTime) null);
            b.parentFolder = (String) m.getParentFolder().orElse(String.valueOf((Object) null));
            b.flags = m.getFlags();
            b.charset = m.getCharset();
            return b;
        }

        private static EmailMessage.EmailMessageBuilder forwardMessage(EmailMessage m) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private static EmailMessage.EmailMessageBuilder replyMessage(EmailMessage m) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private static EmailMessage.EmailMessageBuilder replyAllMessage(EmailMessage m) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public EmailMessage.EmailMessageBuilder from(EmailAddress from) {
            this.from = from;
            return this;
        }

        public EmailMessage.EmailMessageBuilder to(List<EmailAddress> addresses) {
            this.to = addresses;
            return this;
        }

        public EmailMessage.EmailMessageBuilder to(EmailAddress... addresses) {
            return this.to(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addTo(EmailAddress... addresses) {
            return this.addTo(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addTo(List<EmailAddress> addresses) {
            this.to.addAll(addresses);
            return this;
        }

        public EmailMessage.EmailMessageBuilder cc(List<EmailAddress> addresses) {
            this.cc = addresses;
            return this;
        }

        public EmailMessage.EmailMessageBuilder cc(EmailAddress... addresses) {
            return this.cc(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addCc(EmailAddress... addresses) {
            return this.addCc(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addCc(List<EmailAddress> addresses) {
            this.cc.addAll(addresses);
            return this;
        }

        public EmailMessage.EmailMessageBuilder bcc(List<EmailAddress> addresses) {
            this.bcc = addresses;
            return this;
        }

        public EmailMessage.EmailMessageBuilder bcc(EmailAddress... addresses) {
            return this.bcc(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addBcc(EmailAddress... addresses) {
            return this.addBcc(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addBcc(List<EmailAddress> addresses) {
            this.bcc.addAll(addresses);
            return this;
        }

        public EmailMessage.EmailMessageBuilder replyTo(List<EmailAddress> addresses) {
            this.replyTo = addresses;
            return this;
        }

        public EmailMessage.EmailMessageBuilder replyTo(EmailAddress... addresses) {
            return this.replyTo(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addReplyTo(EmailAddress... addresses) {
            return this.addReplyTo(Arrays.asList(addresses));
        }

        public EmailMessage.EmailMessageBuilder addReplyTo(List<EmailAddress> addresses) {
            this.replyTo.addAll(addresses);
            return this;
        }

        public EmailMessage.EmailMessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailMessage.EmailMessageBuilder text(String body) {
            this.text = body;
            return this;
        }

        public EmailMessage.EmailMessageBuilder html(String html) {
            this.html = html;
            return this;
        }

        public EmailMessage.EmailMessageBuilder inlineFiles(List<? extends EmailAttachment> inlineFiles) {
            this.inlineFiles.clear();
            this.inlineFiles.addAll(inlineFiles);
            return this;
        }

        public EmailMessage.EmailMessageBuilder inlineFiles(EmailAttachment... inlineFiles) {
            return this.inlineFiles(Arrays.asList(inlineFiles));
        }

        public EmailMessage.EmailMessageBuilder addInlineFiles(List<EmailAttachment> inlineFiles) {
            this.inlineFiles.addAll(inlineFiles);
            return this;
        }

        public EmailMessage.EmailMessageBuilder addInlineFiles(EmailAttachment... inlineFiles) {
            return this.addInlineFiles(Arrays.asList(inlineFiles));
        }

        public EmailMessage.EmailMessageBuilder attachments(List<? extends EmailAttachment> attachments) {
            attachments.clear();
            this.attachments.addAll(attachments);
            return this;
        }

        public EmailMessage.EmailMessageBuilder attachments(EmailAttachment... attachments) {
            return this.attachments(Arrays.asList(attachments));
        }

        public EmailMessage.EmailMessageBuilder addAttachments(List<EmailAttachment> attachments) {
            this.attachments.addAll(attachments);
            return this;
        }

        public EmailMessage.EmailMessageBuilder EmailAttachment(EmailAttachment... attachments) {
            this.attachments.addAll(Arrays.asList(attachments));
            return this;
        }

        public EmailMessage.EmailMessageBuilder date(ZonedDateTime date) {
            this.date = date;
            return this;
        }

        public EmailMessage.EmailMessageBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EmailMessage.EmailMessageBuilder parentFolder(String parentFolder) {
            this.parentFolder = parentFolder;
            return this;
        }

        public EmailMessage.EmailMessageBuilder addFlags(EmailFlag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public EmailMessage.EmailMessageBuilder addFlags(Set<EmailFlag> flags) {
            this.flags.addAll(flags);
            return this;
        }

        public EmailMessage.EmailMessageBuilder removeFlags(EmailFlag... flags) {
            EmailFlag[] emailFlags = flags;
            int length = flags.length;

            for (int i = 0; i < length; ++i) {
                EmailFlag f = emailFlags[i];
                this.flags.remove(f);
            }

            return this;
        }

        public EmailMessage.EmailMessageBuilder charset(String charset) {
            this.charset = charset;
            return this;
        }

        public EmailMessage build() {
            if (this.text == null) {
                if (this.html != null) {
                    this.text = EmailUtils.htmlToText(this.html);
                } else {
                    this.text = "";
                }
            }

            if (this.replyTo.isEmpty()) {
                this.replyTo.add(this.from);
            }

            return new EmailMessage(this.id, this.from, Collections.unmodifiableList(this.to), Collections.unmodifiableList(this.cc), Collections.unmodifiableList(this.bcc), Collections.unmodifiableList(this.replyTo), this.subject, this.text, this.html, this.inlineFiles,
                    Collections.unmodifiableList(this.attachments), this.date, this.parentFolder, Collections.unmodifiableSet(this.flags), this.charset);
        }
    }
}
