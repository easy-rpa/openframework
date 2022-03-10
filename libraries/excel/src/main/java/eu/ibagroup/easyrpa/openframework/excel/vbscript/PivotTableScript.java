package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.PivotTableParams;

/**
 * VB script to create/update pivot table for Excel file.
 * <br>
 * This class uses <code>pivotTable.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>
 * cscript "C:/scripts/pivotTable.vbs" "C:/Users/user1/AppData/Local/Temp/User Report.xlsx"
 * "CREATE" "Pivot Table 1", "'Pivot Tables'!R5C2", "Passengers!R3C3:R894C14",
 * "Survived:Survived" "" "Sex:Sex,Class:Class" "Passenger Id:Passengers:COUNT"
 * </code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: action name that needs to be performed for pivot table</li>
 *     <li>Argument 2: name of pivot table</li>
 *     <li>Argument 3: destination position where pivot table will be placed</li>
 *     <li>Argument 4: source range for pivot table</li>
 *     <li>
 *         Argument 5: filter fields definition. Value is divided with colon.
 *                     First part is source field name, second - display name.
 *     </li>
 *     <li>
 *         Argument 6: column fields definition. Value is divided with colon.
 *                     First part is source field name, second - display name.
 *     </li>
 *     <li>
 *         Argument 7: row fields definition. Value is divided with colon.
 *                     First part is source field name, second - display name.
 *     </li>
 *     <li>
 *         Argument 8: value fields definition. Value is divided with colon.
 *                     First part is source field name, second - display name, third - summarize type
 *     </li>
 * </ul>
 */
public class PivotTableScript extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/pivotTable.vbs";


    public PivotTableScript(ScriptAction action, PivotTableParams params) {
        super(VBS_FILE_PATH, action.toString(), params.getArgsString());
    }

    public enum ScriptAction {
        CREATE,
        UPDATE
    }
}
