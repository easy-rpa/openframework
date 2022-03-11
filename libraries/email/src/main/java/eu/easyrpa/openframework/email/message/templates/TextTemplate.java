package eu.easyrpa.openframework.email.message.templates;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for any textual template.
 * <p>
 * It keeps all parameters necessary for compilation of the template.
 *
 * @see FreeMarkerTemplate
 */
public abstract class TextTemplate {

    /**
     * Map with properties that is intended to use for substitution of variables within this template.
     */
    protected Map<String, Object> properties;

    /**
     * Constructs a new TextTemplate.
     *
     * @param props the map with properties that is intended to use for substitution of variables within this template.
     */
    public TextTemplate(Map<String, Object> props) {
        if (props != null) {
            this.properties = props;
        } else {
            this.properties = new HashMap<>();
        }
    }

    /**
     * Puts all properties of given map into properties of this template.
     *
     * @param props the map with properties to put.
     * @return this object to allow joining of methods calls into chain.
     */
    public TextTemplate put(Map<String, Object> props) {
        props.forEach(this::put);
        return this;
    }

    /**
     * Puts a new property into properties of this template.
     *
     * @param varName the template variable name for which this property defines a value.
     * @param value   the actual value that should be used instead of variable during compilation of this template.
     * @return this object to allow joining of methods calls into chain.
     */
    @SuppressWarnings("unchecked")
    public TextTemplate put(String varName, Object value) {
        Object currentData = properties.get(varName);
        if (currentData != null) {
            List<Object> list;
            if (currentData instanceof List) {
                list = (List<Object>) currentData;
            } else {
                list = new ArrayList<>();
                list.add(currentData);
                properties.put(varName, list);
            }
            list.add(value);
        } else {
            properties.put(varName, value);
        }
        return this;
    }

    /**
     * Compiles this template.
     *
     * @return the string with result of this template compilation using specified properties.
     */
    public abstract String compile();

    /**
     * Compiles this template and writes result using given writer.
     *
     * @param writer the {@link Writer} used for writing the result of compilation.
     */
    public abstract void compileAndWrite(Writer writer);
}
