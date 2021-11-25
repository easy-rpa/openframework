package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.CellRange;

/**
 * VB script to filter records of table on Excel file sheet.
 * <br>
 * This class uses <code>filter.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>cscript "C:/scripts/filter.vbs" "C:/Users/user1/AppData/Local/Temp/document.xlsx" "Passengers!C3:N893" "2" "Adams,.*" "D4:D893"</code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: range of cells that represent table on some specific sheet</li>
 *     <li>Argument 2: 1-based table column index</li>
 *     <li>Argument 3: value pattern that define subset of values that need to be displayed</li>
 *     <li>Argument 4: range of cells where corresponding values should be looked up using value pattern above</li>
 * </ul>
 */
public class Filter extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/filter.vbs";

    /**
     * @param table         -
     * @param columnIndex   -
     * @param filterPattern - the .Pattern property of the VBScript RegExp Objects.
     *                      More info on https://developer.rhino3d.com/guides/rhinoscript/vbscript-regexp-objects/
     * @param valuesRange   -
     */
    public Filter(CellRange table, int columnIndex, String filterPattern, CellRange valuesRange) {
        super(VBS_FILE_PATH, table.formatAsString(), "" + columnIndex, filterPattern, valuesRange.formatAsString());
    }
}
