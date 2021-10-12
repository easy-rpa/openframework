package eu.ibagroup.easyrpa.openframework.email.core.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FreeMarkerTemplate extends TextTemplate {

    private Template template;

    public FreeMarkerTemplate(InputStream inputStream) throws IOException {
        this(IOUtils.toString(inputStream, StandardCharsets.UTF_8), new HashMap<String, Object>());
    }

    public FreeMarkerTemplate(InputStream inputStream, String encoding) throws IOException {
        this(IOUtils.toString(inputStream, encoding), new HashMap<String, Object>());
    }

    public FreeMarkerTemplate(String templateText, HashMap<String, Object> map) throws IOException {
        super(map);
        init(templateText);
    }

    @Override
    public String compile() throws IOException, TemplateException {
        Writer writer = new StringWriter();
        template.process(scopes, writer);
        writer.flush();
        return writer.toString();
    }

    @Override
    public void compileAndWrite(Writer writer) throws IOException, TemplateException {
        template.process(scopes, writer);
        writer.flush();
        writer.close();
    }

    private void init(String templateText) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        StringReader reader = new StringReader(templateText);
        this.template = new Template(this.templateName, reader, cfg);
    }
}
