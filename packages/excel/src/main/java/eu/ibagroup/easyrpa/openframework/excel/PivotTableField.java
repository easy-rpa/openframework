package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.PivotValueSumType;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PivotTableField {

    private static final String PROPS_DELIMITER = ":";

    private String name;

    private String sourceField;

    private PivotValueSumType summarizeType;

    public PivotTableField(String sourceField) {
        this.sourceField = sourceField;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name == null) {
            return summarizeType != null
                    ? String.format("%s %s", summarizeType.getPrefix(), sourceField)
                    : sourceField;
        }
        return name;
    }

    public String getSourceField() {
        return sourceField;
    }

    public void setSummarizeType(PivotValueSumType summarizeType) {
        this.summarizeType = summarizeType;
    }

    public PivotValueSumType getSummarizeType() {
        return summarizeType;
    }

    @Override
    public String toString() {
        return Stream.of(getSourceField(), getName(), getSummarizeType())
                .filter(Objects::nonNull).map(Objects::toString)
                .collect(Collectors.joining(PROPS_DELIMITER));
    }
}
