package eu.easyrpa.openframework.excel.vbscript;

import eu.easyrpa.openframework.excel.CellRef;
import eu.easyrpa.openframework.excel.CellRange;

/**
 * VB script to move specific columns within Excel file sheet.
 * <br>
 * This class uses <code>columnsMove.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>cscript "C:/scripts/columnsMove.vbs" "C:/Users/user1/AppData/Local/Temp/document.xlsx" "Sheet0!D:F" "K"</code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: range of columns that should be moved</li>
 *     <li>Argument 2: position of column before which columns should be moved</li>
 * </ul>
 */
public class ColumnsMove extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/columnsMove.vbs";

    public ColumnsMove(CellRange columnsRange, CellRef beforeColumn) {
        super(VBS_FILE_PATH, columnsRange.formatAsString(), beforeColumn.formatAsString());
    }
}
