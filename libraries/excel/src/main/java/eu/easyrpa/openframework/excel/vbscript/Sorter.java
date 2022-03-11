package eu.easyrpa.openframework.excel.vbscript;

import eu.easyrpa.openframework.excel.CellRange;
import eu.easyrpa.openframework.excel.constants.SortDirection;

/**
 * VB script to sort cells on Excel file sheet.
 * <br>
 * This class uses <code>sort.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>cscript "C:/scripts/sort.vbs" "C:/Users/user1/AppData/Local/Temp/document.xlsx" "Sheet0!D1:D893" "ASC"</code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: range of cells that should be sorted</li>
 *     <li>Argument 2: sort direction. Possible values: 'ASC' or 'DESC'</li>
 * </ul>
 */
public class Sorter extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/sort.vbs";

    public Sorter(CellRange range, SortDirection direction) {
        super(VBS_FILE_PATH, range.formatAsString(), direction.toString());
    }
}
