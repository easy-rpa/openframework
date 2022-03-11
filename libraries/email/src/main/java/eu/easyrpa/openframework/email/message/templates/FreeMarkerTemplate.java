package eu.easyrpa.openframework.email.message.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a textual template based on FreeMarker template language.
 */
public class FreeMarkerTemplate extends TextTemplate {

    /**
     * FreeMaker template object.
     */
    private Template template;

    /**
     * Constructs a new FreeMarkerTemplate with given template text.
     *
     * @param inputStream the {@link InputStream} that provides template text on FreeMarker template language
     *                    with UTF-8 charset.
     * @throws IOException in case of some errors during reading and parsing of given template text.
     */
    public FreeMarkerTemplate(InputStream inputStream) throws IOException {
        this(IOUtils.toString(inputStream, StandardCharsets.UTF_8), new HashMap<>());
    }

    /**
     * Constructs a new FreeMarkerTemplate with given template text.
     *
     * @param inputStream the {@link InputStream} that provides template text on FreeMarker template language.
     * @param charsetName the name of template text charset.
     * @throws IOException in case of some errors during reading and parsing of given template text.
     */
    public FreeMarkerTemplate(InputStream inputStream, String charsetName) throws IOException {
        this(IOUtils.toString(inputStream, charsetName), new HashMap<>());
    }

    /**
     * Constructs a new FreeMarkerTemplate with given template text.
     *
     * @param templateText the string with template text on FreeMarker template language.
     * @param map          the map with properties that is intended to use for substitution of variables within
     *                     this template.
     * @throws IOException in case of some errors during reading and parsing of given template text.
     */
    public FreeMarkerTemplate(String templateText, Map<String, Object> map) throws IOException {
        super(map);
        init(templateText);
    }

    /**
     * Compiles this template.
     *
     * @return the string with result of this template compilation using specified properties.
     */
    @Override
    public String compile() {
        Writer writer = new StringWriter();
        compileAndWrite(writer);
        return writer.toString();
    }

    /**
     * Compiles this template and writes result using given writer.
     *
     * @param writer the {@link Writer} used for writing the result of compilation.
     */
    @Override
    public void compileAndWrite(Writer writer) {
        try {
            template.process(properties, writer);
            writer.flush();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("FreeMarker template compilation has failed.", e);
        }
    }

    /**
     * Initializes FreeMarker template object based on given template text.
     *
     * @param templateText the string with template text.
     * @throws IOException in case of some errors during parsing of given template text.
     */
    private void init(String templateText) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        StringReader reader = new StringReader(templateText);
        this.template = new Template("", reader, cfg);
    }
}
