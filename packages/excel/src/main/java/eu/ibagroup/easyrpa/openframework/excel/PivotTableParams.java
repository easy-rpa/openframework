package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.PivotValueSumType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PivotTableParams {

    private static final String FIELDS_DELIMITER = ",";

    private String pivotTableName;
    private String pivotSheetName;
    private CellRef position;
    private CellRange sourceRange;
    private List<PivotTableField> filters = new ArrayList<>();
    private List<PivotTableField> rows = new ArrayList<>();
    private List<PivotTableField> columns = new ArrayList<>();
    private List<PivotTableField> values = new ArrayList<>();

    private PivotTableParams(String pivotTableName) {
        this.pivotTableName = pivotTableName;
    }

    public static PivotTableParams create(String pivotTableName) {
        return new PivotTableParams(pivotTableName);
    }

    public PivotTableParams position(String startCellRef) {
        position = new CellRef(startCellRef);
        return this;
    }

    public PivotTableParams position(CellRef startCellRef) {
        position = startCellRef;
        return this;
    }

    public PivotTableParams position(int startRow, int startCol) {
        position = new CellRef(startRow, startCol);
        return this;
    }

    public PivotTableParams source(String sheetName, int firstRow, int firstCol, int lastRow, int lastCol) {
        this.sourceRange = new CellRange(sheetName, firstRow, firstCol, lastRow, lastCol);
        return this;
    }

    public PivotTableParams source(CellRange sourceRange) {
        this.sourceRange = sourceRange;
        return this;
    }

    public PivotTableParams source(Table<?> sourceTable) {
        sourceRange = new CellRange(sourceTable.getSheet().getName(), sourceTable.getHeaderTopRow(),
                sourceTable.getHeaderLeftCol(), sourceTable.getBottomRow(), sourceTable.getHeaderRightCol());
        return this;
    }

    public PivotTableParams filter(String sourceField) {
        filters.add(new PivotTableField(sourceField));
        return this;
    }

    public PivotTableParams column(String sourceField) {
        columns.add(new PivotTableField(sourceField));
        return this;
    }

    public PivotTableParams row(String sourceField) {
        rows.add(new PivotTableField(sourceField));
        return this;
    }

    public PivotTableParams value(String customName, String sourceField, PivotValueSumType summarizeType) {
        PivotTableField field = new PivotTableField(sourceField);
        field.setName(customName);
        field.setSummarizeType(summarizeType);
        values.add(field);
        return this;
    }

    public PivotTableParams value(String sourceField, PivotValueSumType summarizeType) {
        PivotTableField field = new PivotTableField(sourceField);
        field.setSummarizeType(summarizeType);
        values.add(field);
        return this;
    }

    public String getArgsString() {
        return String.format("\"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"",
                pivotTableName,
                position != null ? position.formatAsRowColString() : pivotSheetName,
                sourceRange != null ? sourceRange.formatAsRowColString() : "",
                filters.stream().map(PivotTableField::toString).collect(Collectors.joining(FIELDS_DELIMITER)),
                columns.stream().map(PivotTableField::toString).collect(Collectors.joining(FIELDS_DELIMITER)),
                rows.stream().map(PivotTableField::toString).collect(Collectors.joining(FIELDS_DELIMITER)),
                values.stream().map(PivotTableField::toString).collect(Collectors.joining(FIELDS_DELIMITER)));
    }

    protected void setSheetName(String sheetName) {
        pivotSheetName = sheetName;
        if (position != null) {
            position.setSheetName(sheetName);
        }
    }

    protected void checkPosition(){
        if(position == null){
            position = new CellRef(pivotSheetName, 0, 0);
        }
    }
}
