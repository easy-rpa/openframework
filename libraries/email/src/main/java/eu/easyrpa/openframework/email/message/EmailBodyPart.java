package eu.easyrpa.openframework.email.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.easyrpa.openframework.email.message.templates.FreeMarkerTemplate;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a part of email message body with specific content type.
 */
public class EmailBodyPart {

    private static final String FREEMARKER_FILE_EXTENSION = ".ftl";

    /**
     * The MIME type of email body part in simple text format.
     */
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    /**
     * The MIME type of email body part in HTML format.
     */
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    /**
     * The MIME type of this email body part content.
     */
    private String contentType;

    /**
     * The string with content of this email body part.
     */
    private String content;

    /**
     * Constructs a new email body part.
     *
     * @param content     the string with email body part content.
     * @param contentType the string with MIME-type of email body part content.
     */
    @JsonCreator
    public EmailBodyPart(@JsonProperty("content") String content,
                         @JsonProperty("contentType") String contentType) {
        this.contentType = contentType;
        setContent(content);
    }

    /**
     * Gets the MIME type of this email body part content.
     *
     * @return the string with MIME-type of this email body part content.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the MIME type of this email body part content.
     *
     * @param contentType the string with MIME-type to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the content of this email body part.
     *
     * @return the string with content of this email body part.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets compiled content of this email body part using given properties.
     * <p>
     * The result of this method will be different from result of {@link #getContent()} if the {@link #content} is
     * a template on FreeMarker template language.
     *
     * @param properties the map with values of variables used within content template.
     * @return the string with compiled content of this email body part.
     * @see FreeMarkerTemplate
     */
    public String getContent(Map<String, Object> properties) {
        try {
            return new FreeMarkerTemplate(content, properties).compile();
        } catch (Exception e) {
            throw new EmailMessagingException("Email message body compilation has failed.", e);
        }
    }

    /**
     * Sets the content for this email body part.
     * <p>
     * Instead of actual string with content this method accepts path to FreeMarker Template File (*.ftl) in the
     * resources of current RPA process module. In this case the content of the .ftl file will be used as content for
     * this email body part.
     *
     * @param content the string with content to set or path to resource .ftl file with content to set.
     */
    public void setContent(String content) {
        if (content != null && content.endsWith(FREEMARKER_FILE_EXTENSION)) {
            try {
                content = IOUtils.toString(getClass().getResourceAsStream(content), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new EmailMessagingException(String.format("Resource file '%s' not found", content), e);
            }
        }
        this.content = content;
    }

    /**
     * Checks whether the content of this email body part has simple text format.
     *
     * @return <code>true</code> if the content for this email body part has simple text format and
     * <code>false</code> otherwise.
     */
    @JsonIgnore
    public boolean isText() {
        return CONTENT_TYPE_TEXT_PLAIN.equals(contentType);
    }

    /**
     * Checks whether the content of this email body part has HTML format.
     *
     * @return <code>true</code> if the content for this email body part has HTML format and <code>false</code>
     * otherwise.
     */
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
}