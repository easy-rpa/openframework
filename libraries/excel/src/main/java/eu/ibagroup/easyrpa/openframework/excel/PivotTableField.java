package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.PivotValueSumType;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Keeps parameters necessary to define specific pivot table field.
 */
public class PivotTableField {

    /**
     * Delimiter that is used to separate field parameters in arguments string for VBS.
     *
     * @see eu.ibagroup.easyrpa.openframework.excel.vbscript.PivotTableScript
     */
    private static final String PROPS_DELIMITER = ":";

    /**
     * Name of this field displayed in pivot table.
     */
    private String name;

    /**
     * Name of source field. This a name of related column in source data table.
     */
    private String sourceField;

    /**
     * Summarize type that defines way of calculation for value field. This should be specified if this field is a
     * value field of pivot table.
     */
    private PivotValueSumType summarizeType;

    /**
     * Creates a new parameters object for pivot table field.
     *
     * @param sourceField name of related source field.
     */
    public PivotTableField(String sourceField) {
        this.sourceField = sourceField;
    }

    /**
     * Sets custom name for this field that will be displayed in pivot table.
     *
     * @param name the custom name of this field to display in pivot table.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p> Gets name for this field that will be displayed in pivot table.</p>
     *
     * <p>If custom name to display in pivot table is not specified this method returns default name.</p>
     * <p>
     * For filter, row and column fields:
     * <pre>
     *     {@code "<sourceField>"}
     * </pre>
     * <p>
     * For value fields:
     * <pre>
     *     {@code "<prefix of summarizeType> <sourceField>"}
     * </pre>
     *
     * @return name of this field to display in pivot table.
     */
    public String getName() {
        if (name == null) {
            return summarizeType != null
                    ? String.format("%s %s", summarizeType.getPrefix(), sourceField)
                    : sourceField;
        }
        return name;
    }

    /**
     * Gets name of related source field.
     * <br>
     * Pivot table source field is a column in source data table.
     *
     * @return name of related source field.
     */
    public String getSourceField() {
        return sourceField;
    }

    /**
     * Sets summarize type that defines way of calculation for value field. his should be specified if this field is a
     * value field of pivot table.
     *
     * @param summarizeType required way of calculation for value field.
     * @see PivotValueSumType
     */
    public void setSummarizeType(PivotValueSumType summarizeType) {
        this.summarizeType = summarizeType;
    }

    /**
     * Gets summarize type that defines way of calculation for value field.
     *
     * @return summarize type that defines way of calculation for value field or <code>null</code> if this field is
     * not value field.
     * @see PivotValueSumType
     */
    public PivotValueSumType getSummarizeType() {
        return summarizeType;
    }

    /**
     * Returns string presentation of this field with all specified parameters to include into arguments string for VBS.
     *
     * @return string presentation of this field with all specified parameters.
     * @see eu.ibagroup.easyrpa.openframework.excel.vbscript.PivotTableScript
     */
    @Override
    public String toString() {
        return Stream.of(getSourceField(), getName(), getSummarizeType())
                .filter(Objects::nonNull).map(Objects::toString)
                .collect(Collectors.joining(PROPS_DELIMITER));
    }
}
