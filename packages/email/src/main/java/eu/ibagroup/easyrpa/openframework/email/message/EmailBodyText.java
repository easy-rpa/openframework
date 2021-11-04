package eu.ibagroup.easyrpa.openframework.email.message;

public class EmailBodyText implements EmailBodyPart {

    private String content;

    public EmailBodyText() {
    }

    public EmailBodyText(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
