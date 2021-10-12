package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.SpreadsheetDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Example of script execution : 'cscript createPivoteTable.vbs
 * "C:\Scripts\2018-03-31.xlsx" "MTM!R7C1:R100C43" "Total!R3C1" "PivotTable1"
 * "AH,Status" "" "" "Asset,Liabiity:COUNT"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: source - the source for pivot
 * table ('from' part) Argument 2: target - the destination for pivot table
 * Argument 3: tableName - pivot Table name Argument 4: filter Argument 5:
 * ColumnLabels Argument 6: RowLabel Argument 7: Values
 */
public class PivotTableBuilder extends VBScript {

    /**
     * Name of the VB script to create pivot table.
     */
    public static final String VBS_FILE_PATH = "vbscript/createPivoteTable.vbs";

    private String source;
    private String target;
    private String tableName;
    private final List<PivotTableField> fields = new ArrayList<>();

    /**
     * Construct empty PivotTableBuilder script. Methods to add parameters must be
     * used before perform();
     */
    public PivotTableBuilder() {
        this("", "", "");
    }

    /**
     * Builder for new Pivot Table.
     *
     * @param source    -
     * @param target    -
     * @param tableName -
     */
    public PivotTableBuilder(String source, String target, String tableName) {
        super(VBS_FILE_PATH);
        this.source = source;
        this.target = target;
        this.tableName = tableName;
    }

    public PivotTableBuilder source(String source) {
        this.source = source;
        return this;
    }

    public PivotTableBuilder target(String target) {
        this.target = target;
        return this;
    }

    public PivotTableBuilder tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public PivotTableBuilder addField(String name, PivotTableFieldType type) {
        fields.add(new PivotTableField(name, type));
        return this;
    }

    public PivotTableBuilder addValue(String name) {
        fields.add(new PivotTableField(name, PivotTableFieldType.VALUE));
        return this;
    }

    public PivotTableBuilder addColumnLabel(String name) {
        fields.add(new PivotTableField(name, PivotTableFieldType.COLUMN));
        return this;
    }

    public PivotTableBuilder addFilter(String name) {
        fields.add(new PivotTableField(name, PivotTableFieldType.FILTER));
        return this;
    }

    public PivotTableBuilder addRowLabel(String name) {
        fields.add(new PivotTableField(name, PivotTableFieldType.ROW));
        return this;
    }

    /**
     * Execute VBS_FILE_PATH on spreadsheet specified and reload it.
     *
     * @param spreadsheet -
     */
    public void createPivotTable(SpreadsheetDocument spreadsheet) {
        spreadsheet.createSheet(getTargetSheet());
        spreadsheet.runScript(this);
    }

    private String getTargetSheet() {
        System.out.println(target.split("!")[0]);
        return target.split("!")[0];
    }

    private String getRowLabels() {
        return getFields(PivotTableFieldType.ROW);
    }

    private String getColumnLabels() {
        return getFields(PivotTableFieldType.COLUMN);
    }

    private String getFilters() {
        return getFields(PivotTableFieldType.FILTER);
    }

    private String getValues() {
        return getFields(PivotTableFieldType.VALUE);
    }

    private String getFields(PivotTableFieldType type) {
        StringBuilder fieldsStr = new StringBuilder();
        fields.stream().filter(f -> f.getType().equals(type)).forEach(f -> fieldsStr.append(f.getName()).append(","));
        if (fieldsStr.length() == 0) {
            return "";
        }
        fieldsStr.setLength(fieldsStr.length() - 1);
        return fieldsStr.toString();
    }

    public static class PivotTableField {
        String name;
        PivotTableFieldType type;

        public String getName() {
            return name;
        }

        public PivotTableFieldType getType() {
            return type;
        }

        PivotTableField(String name, PivotTableFieldType type) {
            this.name = name;
            this.type = type;
        }
    }

    public enum PivotTableFieldType {
        FILTER, COLUMN, ROW, VALUE
    }

    /**
     * Updates remote spreadsheet by applying CREATE_PIVOT_TABLE_SCRIPT
     */
    public void perform(String filePath) {

        // The parameters builds on fly and must be specified before perform
        // Store old parameters
        List<String> oldParameters = getParameters();

        // Create new parameters
        String[] newArgs = {source, target, tableName, getFilters(), getColumnLabels(), getRowLabels(), getValues()};
        params(newArgs);

        // Perform script
        super.perform(filePath);

        // Restore old parameters
        setParameters(oldParameters);
    }

}
