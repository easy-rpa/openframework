package eu.ibagroup.easyrpa.openframework.email.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.templates.FreeMarkerTemplate;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class EmailBodyPart {

    private static final String FREEMARKER_FILE_EXTENSION = ".ftl";

    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    private String contentType;

    private String content;

    @JsonCreator
    public EmailBodyPart(@JsonProperty("content") String content,
                         @JsonProperty("contentType") String contentType) {
        this.contentType = contentType;
        setContent(content);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public String getContent(Map<String, Object> properties) {
        try {
            return new FreeMarkerTemplate(content, properties).compile();
        } catch (IOException | TemplateException e) {
            throw new EmailMessagingException("Email message body compilation has failed.", e);
        }
    }

    public void setContent(String content) {
        this.content = content != null && content.endsWith(FREEMARKER_FILE_EXTENSION) ? getResourceText(content) : content;
    }

    @JsonIgnore
    public boolean isText() {
        return CONTENT_TYPE_TEXT_PLAIN.equals(contentType);
    }

    @JsonIgnore
    public boolean isHtml() {
        return CONTENT_TYPE_TEXT_HTML.equals(contentType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailBodyPart)) return false;
        EmailBodyPart part = (EmailBodyPart) o;
        return Objects.equals(contentType, part.contentType) &&
                Objects.equals(content, part.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, content);
    }

    @Override
    public String toString() {
        return "EmailBodyPart{" +
                "contentType='" + contentType + '\'' +
                ", content='" + (content != null ?
                content.substring(0, Math.min(20, content.length())).replaceAll("\\s", " ") + (content.length() > 20 ? "..." : "")
                : "") + '\'' +
                '}';
    }

    private String getResourceText(String resourceFilePath) {
        if (resourceFilePath != null) {
            try {
                return IOUtils.toString(getClass().getResourceAsStream(resourceFilePath), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new EmailMessagingException(String.format("Resource file '%s' not found", resourceFilePath), e);
            }
        }
        return "";
    }
}