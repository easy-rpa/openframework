package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.CellRange;

/**
 * VB script to delete specific columns from Excel file sheet.
 * <br>
 * This class uses <code>columnsDelete.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>cscript "C:/scripts/columnsDelete.vbs" "C:/Users/user1/AppData/Local/Temp/document.xlsx" "Sheet0!D:F"</code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: range of columns that should be deleted</li>
 * </ul>
 */
public class ColumnsDelete extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/columnsDelete.vbs";

    public ColumnsDelete(CellRange columnsRange) {
        super(VBS_FILE_PATH, columnsRange.formatAsString());
    }
}
