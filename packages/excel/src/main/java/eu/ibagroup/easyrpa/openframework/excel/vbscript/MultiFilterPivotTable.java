package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * Example of script execution : 'cscript multiFilterPivotTable.vbs
 * "C:\Users\2018-08.xlsx" "Total" "PivotTable2" "GL account" "ALL" "" "83410"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: sheet name Argument 2:
 * pivotTableName Argument 3: pivotTableField Argument 4: pivotTableFilterMode
 * Argument 5: selectItems (comma-separated string) Argument 6: unselectItems
 * (comma-separated string)
 *
 * @author Kulikov
 */
public class MultiFilterPivotTable extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/multiFilterPivotTable.vbs";

    /**
     * Construct empty MultiFilterPivotTable script. Methods to add parameters must
     * be used before perform();
     */
    public MultiFilterPivotTable() {
        this("", "", "");
    }

    /**
     * Construct script with mandatory parameters specified.
     *
     * @param tabName         -
     * @param pivotTableName  -
     * @param pivotTableField -
     */
    public MultiFilterPivotTable(String tabName, String pivotTableName, String pivotTableField) {
        this(tabName, pivotTableName, pivotTableField, "ALL", "", "");
    }

    public MultiFilterPivotTable(String tabName, String pivotTableName, String pivotTableField, String mode,
                                 String select, String unselect) {
        super(VBS_FILE_PATH);
        params(tabName, pivotTableName, pivotTableField, mode, select, unselect);
    }

    public MultiFilterPivotTable tabName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public MultiFilterPivotTable tableName(String pivotTableName) {
        getParameters().set(1, pivotTableName);
        return this;
    }

    public MultiFilterPivotTable tableField(String pivotTableField) {
        getParameters().set(2, pivotTableField);
        return this;
    }

    public MultiFilterPivotTable mode(String mode) {
        getParameters().set(3, mode);
        return this;
    }

    public MultiFilterPivotTable select(String select) {
        getParameters().set(4, select);
        return this;
    }

    public MultiFilterPivotTable unselect(String unselect) {
        getParameters().set(5, unselect);
        return this;
    }

}
