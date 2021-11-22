package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * This script insert column to the spreadsheet file.
 * <p>
 * 'script call example 'cscript "insertColumn.vbs" "C:/Users/users.xlsx" "User
 * list" "B"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: Sheet name (Tab name) Argument
 * 2: Reference to column (letters)
 * <p>
 * insert column to the colRef position and shifts other cells.
 */
public class InsertColumn extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/insertColumn.vbs";

    /**
     * Construct empty InsertColumn script. Methods to add parameters must be used
     * before perform();
     */
    public InsertColumn() {
        this("", "");
    }

    public InsertColumn(String sheetName, String colRef) {
        super(VBS_FILE_PATH);
        params(sheetName, colRef);
    }

    public InsertColumn sheetName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public InsertColumn colRef(String colRef) {
        getParameters().set(1, colRef);
        return this;
    }

}
