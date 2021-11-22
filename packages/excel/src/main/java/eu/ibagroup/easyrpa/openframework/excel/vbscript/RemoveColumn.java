package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * This script remove column from the Excel file.
 * <p>
 *  script call example: cscript "removeColumn.vbs" "C:/Users/users.xlsx" "User list" "5"
 * </p>
 * <p>
 * Argument 0: Excel file to proceed
 * Argument 1: Sheet name (Tab name) Argument
 * 2: Column Index (letters)
 * </p>
 * insert column to the colRef position and shifts other cells.
 */
public class RemoveColumn extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/removeColumn.vbs";

    /**
     * Construct empty RemoveColumn script. Methods to add parameters must be used
     * before perform();
     */
    public RemoveColumn() {
        this("", -1);
    }

    public RemoveColumn(String sheetName, Integer colIndex) {
        super(VBS_FILE_PATH);
        params(sheetName, "" + colIndex);
    }

    public RemoveColumn sheetName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public RemoveColumn colIndex(Integer colIndex) {
        getParameters().set(1, "" + colIndex);
        return this;
    }

}
