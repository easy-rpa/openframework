package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.SpreadsheetDocument;
import org.apache.poi.ss.util.CellReference;

/**
 * Sort column in the tab specified.
 * <p>
 * 'script call example 'cscript sort.vbs
 * "C:\Scripts\VBS\ChartofAccounts984.xlsx" "ChartofAccounts" "B1:B459" "ZtoA"
 */
public class Sorter extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/sort.vbs";

    /**
     * Construct empty Sorter script. Methods to add parameters must be used before
     * perform();
     */
    public Sorter() {
        this("", "");
    }

    /**
     * Sort sheet and range specified
     *
     * @param tabName -
     * @param target  -
     */
    public Sorter(String tabName, String target) {
        this(tabName, target, SortingType.AtoZ);
    }

    public Sorter(String tabName, String target, SortingType sortType) {
        super(VBS_FILE_PATH);
        params(tabName, target, sortType.toString());
    }

    public Sorter(String tabName, String headerCellRef, SortingType sortType, SpreadsheetDocument spreadsheet) {
        this(tabName, getTarget(headerCellRef, spreadsheet), sortType);
    }

    public Sorter tabName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public Sorter target(String target) {
        getParameters().set(1, target);
        return this;
    }

    public Sorter sortAtoZ() {
        getParameters().set(2, SortingType.AtoZ.toString());
        return this;
    }

    public Sorter sortZtoA() {
        getParameters().set(2, SortingType.ZtoA.toString());
        return this;
    }

    public enum SortingType {
        AtoZ, ZtoA
    }

    private static String getTarget(String headerCellRef, SpreadsheetDocument spreadsheet) {
        String lastCellRef = new CellReference(CellReference.convertColStringToIndex(headerCellRef),
                spreadsheet.getActiveSheet().getLastRowNum()).formatAsString();
        return headerCellRef + ':' + lastCellRef;
    }

}
