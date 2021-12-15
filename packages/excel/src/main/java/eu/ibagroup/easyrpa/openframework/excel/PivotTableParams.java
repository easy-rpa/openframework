package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.PivotValueSumType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Keeps parameters necessary to define specific pivot table within Excel Document.
 * <br>
 * This class allows to collect pivot table parameters and convert them to arguments string for
 * {@link eu.ibagroup.easyrpa.openframework.excel.vbscript.PivotTableScript}
 */
public class PivotTableParams {

    /**
     * Delimiter that is used to separate field names in VBS arguments string.
     *
     * @see eu.ibagroup.easyrpa.openframework.excel.vbscript.PivotTableScript
     */
    private static final String FIELDS_DELIMITER = ",";

    /**
     * Name of related pivot table.
     */
    private String pivotTableName;

    /**
     * Name of sheet where related pivot table is located or should be placed.
     */
    private String pivotSheetName;

    /**
     * Reference to the top-left cell of related pivot table on the sheet.
     */
    private CellRef position;

    /**
     * Cells range with sheet name that defines source data for related pivot table.
     */
    private CellRange sourceRange;

    /**
     * List of pivot table fields that should be used for filtering of its data.
     */
    private List<PivotTableField> filters = new ArrayList<>();

    /**
     * List of pivot table fields that should be used as rows.
     */
    private List<PivotTableField> rows = new ArrayList<>();

    /**
     * List of pivot table fields that should be used as column.
     */
    private List<PivotTableField> columns = new ArrayList<>();

    /**
     * List of pivot table fields that should be calculated.
     */
    private List<PivotTableField> values = new ArrayList<>();

    /**
     * Creates a new instance of pivot table parameters. It cannot be called directly. The static method
     * {@link #create(String)} should be used instead.
     *
     * @param pivotTableName name of related pivot table.
     */
    private PivotTableParams(String pivotTableName) {
        this.pivotTableName = pivotTableName;
    }

    /**
     * Creates a new parameters object for pivot table with given name.
     *
     * @param pivotTableName name of related pivot table.
     * @return object that keeping parameters for pivot table with given name.
     */
    public static PivotTableParams create(String pivotTableName) {
        return new PivotTableParams(pivotTableName);
    }

    /**
     * Sets position of related pivot table on the sheet.
     *
     * @param startCellRef reference string to the cell that should be top-left cell of related pivot table on the
     *                     sheet. E.g. "A23".
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams position(String startCellRef) {
        position = new CellRef(startCellRef);
        return this;
    }

    /**
     * Sets position of related pivot table on the sheet.
     *
     * @param startCellRef reference to the cell that should be top-left cell of related pivot table on the sheet.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams position(CellRef startCellRef) {
        position = startCellRef;
        return this;
    }

    /**
     * Sets position of related pivot table on the sheet.
     *
     * @param startRow 0-based row index that defines top-left cell of related pivot table on the sheet.
     * @param startCol 0-based column index that defines top-left cell of related pivot table on the sheet.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams position(int startRow, int startCol) {
        position = new CellRef(startRow, startCol);
        return this;
    }

    /**
     * Sets source data range for related pivot table.
     *
     * @param sheetName name of sheet where source data is contained.
     * @param firstRow  0-based index of top row of the source data range.
     * @param firstCol  0-based index of left column of the source data range.
     * @param lastRow   0-based index of bottom row of the source data range.
     * @param lastCol   0-based index of right column of the source data range.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams source(String sheetName, int firstRow, int firstCol, int lastRow, int lastCol) {
        this.sourceRange = new CellRange(sheetName, firstRow, firstCol, lastRow, lastCol);
        return this;
    }

    /**
     * Sets source data range for related pivot table.
     *
     * @param sourceRange the source data range with specified sheet name.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams source(CellRange sourceRange) {
        this.sourceRange = sourceRange;
        return this;
    }

    /**
     * Sets source data for related pivot table.
     *
     * @param sourceTable the object representing table on the sheet that should be used as source data
     *                    for pivot table.
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams source(Table<?> sourceTable) {
        sourceRange = new CellRange(sourceTable.getSheet().getName(), sourceTable.getHeaderTopRow(),
                sourceTable.getHeaderLeftCol(), sourceTable.getBottomRow(), sourceTable.getHeaderRightCol());
        return this;
    }

    /**
     * Sets field that should be used for filtering of related pivot table data.
     *
     * @param sourceField name of source field (source table column).
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams filter(String sourceField) {
        filters.add(new PivotTableField(sourceField));
        return this;
    }

    /**
     * Sets field that should be used as column in related pivot table.
     *
     * @param sourceField name of source field (source table column).
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams column(String sourceField) {
        columns.add(new PivotTableField(sourceField));
        return this;
    }

    /**
     * Sets field that should be used as row in related pivot table.
     *
     * @param sourceField name of source field (source table column).
     * @return this parameters object to allow joining of methods calls into chain.
     */
    public PivotTableParams row(String sourceField) {
        rows.add(new PivotTableField(sourceField));
        return this;
    }

    /**
     * Sets field that should be used for calculation.
     *
     * @param customName    the display name of the filed in related pivot table.
     * @param sourceField   name of source field (source table column).
     * @param summarizeType defines necessary type of calculations.
     * @return this parameters object to allow joining of methods calls into chain.
     * @see PivotValueSumType
     */
    public PivotTableParams value(String customName, String sourceField, PivotValueSumType summarizeType) {
        PivotTableField field = new PivotTableField(sourceField);
        field.setName(customName);
        field.setSummarizeType(summarizeType);
        values.add(field);
        return this;
    }

    /**
     * Sets field that should be used for calculation.
     *
     * @param sourceField   name of source field (source table column).
     * @param summarizeType defines necessary type of calculations.
     * @return this parameters object to allow joining of methods calls into chain.
     * @see PivotValueSumType
     */
    public PivotTableParams value(String sourceField, PivotValueSumType summarizeType) {
        PivotTableField field = new PivotTableField(sourceField);
        field.setSummarizeType(summarizeType);
        values.add(field);
        return this;
    }

    /**
     * Gets arguments string for {@link eu.ibagroup.easyrpa.openframework.excel.vbscript.PivotTableScript} that includes
     * all specified within this object parameters.
     *
     * @return arguments string for {@link eu.ibagroup.easyrpa.openframework.excel.vbscript.PivotTableScript}.
     */
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

    /**
     * Sets sheet name where the related pivot table should be placed or looked up.
     *
     * @param sheetName name of sheet where the related pivot table should be placed or looked up.
     */
    protected void setSheetName(String sheetName) {
        pivotSheetName = sheetName;
        if (position != null) {
            position.setSheetName(sheetName);
        }
    }

    /**
     * Checks to be sure that pivot table position on the sheet is defined.
     * <br>
     * If position is not defined sets default position with row index 0 and column index 0.
     */
    protected void checkPosition() {
        if (position == null) {
            position = new CellRef(pivotSheetName, 0, 0);
        }
    }
}
