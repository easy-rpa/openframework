package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * Example of script execution : 'cscript filterPivotTable.vbs
 * "C:\Scripts\2018-03-31.xlsx" "Total" "PivotTable1" "AH:Actual,Status:BO
 * validated"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: Sheet name (Tab name) Argument
 * 2: Pivot Table Name Argument 3: filterPattern
 */
public class FilterPivotTable extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/filterPivotTable.vbs";

    /**
     * Construct empty FilterPivotTable script. Methods to add parameters must be
     * used before perform();
     */
    public FilterPivotTable() {
        this("", "", "");
    }

    public FilterPivotTable(String tabName, String pivotTableName, String filterPattern) {
        super(VBS_FILE_PATH);
        params(tabName, pivotTableName, filterPattern);
    }

    public FilterPivotTable tabName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public FilterPivotTable tableName(String pivotTableName) {
        getParameters().set(1, pivotTableName);
        return this;
    }

    public FilterPivotTable pattern(String filterPattern) {
        getParameters().set(2, filterPattern);
        return this;
    }
}
