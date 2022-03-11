package eu.easyrpa.openframework.excel.function;

import eu.easyrpa.openframework.excel.annotations.ExcelColumn;

import java.util.List;

/**
 * Allows to extract value for some specific field of record from table row data.
 *
 * @see ExcelColumn#mapper()
 */
@FunctionalInterface
public interface FieldMapper {

    /**
     * Maps table row data to the value of given field.
     *
     * @param fieldName  the name of field the value of which is necessary to get.
     * @param values     list of cell values representing row data.
     * @param valueIndex index of column related to the field and thus index of possible field value in the list
     *                   of values. This index is present <b>only if {@link ExcelColumn#name()} is specified</b>
     *                   for the field. Otherwise this index equals <b><code>-1</code></b>.
     * @return value of the field.
     */
    Object map(String fieldName, List<Object> values, int valueIndex);
}
