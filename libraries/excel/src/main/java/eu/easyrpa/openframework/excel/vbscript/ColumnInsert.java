package eu.easyrpa.openframework.excel.vbscript;

import eu.easyrpa.openframework.excel.CellRef;

/**
 * VB script to insert empty column to Excel file sheet.
 * <br>
 * This class uses <code>columnInsert.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>cscript "C:/scripts/columnInsert.vbs" "C:/Users/user1/AppData/Local/Temp/document.xlsx" "Sheet0!D"</code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: position where column should be inserted</li> *
 * </ul>
 */
public class ColumnInsert extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/columnInsert.vbs";

    public ColumnInsert(CellRef position) {
        super(VBS_FILE_PATH, position.formatAsString());
    }
}
