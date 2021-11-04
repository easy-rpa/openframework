package eu.ibagroup.easyrpa.openframework.email.message;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EmailBodyTemplate implements EmailBodyPart {

    private String templatePath;

    public EmailBodyTemplate() {
    }

    public EmailBodyTemplate(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplateText() {
        if (templatePath != null) {
            try {
                return IOUtils.toString(getClass().getResourceAsStream(templatePath), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new EmailMessagingException("Template file not found", e);
            }
        }
        return "";
    }
}
