package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * Example of script execution : 'cscript filter.vbs
 * "C:\Scripts\VBS\ChartofAccounts984.xlsx" "ChartofAccounts" "19***" "A1:H460"
 * "B2:B460" "2"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: Sheet name (Tab name) Argument
 * 2: filterPattern Argument 3: target Range Argument 4: filterColRange Argument
 * 5: fieldIndex 'The integer offset of the field on which you want to base the
 * filter (from the left of the list; the leftmost field is field one).
 */
public class Filter extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/filter.vbs";

    /**
     * Construct empty Filter. Methods to add parameters must be used before
     * perform();
     */
    public Filter() {
        this("", "", "", "");
    }

    /**
     * @param tabName        -
     * @param filterPattern  the .Pattern property of the VBScript RegExp Objects.
     *                       More info on
     *                       https://developer.rhino3d.com/guides/rhinoscript/vbscript-regexp-objects/
     * @param target         -
     * @param filterColRange -
     */
    public Filter(String tabName, String filterPattern, String target, String filterColRange) {
        this(tabName, filterPattern, target, filterColRange, "1");
    }

    public Filter(String tabName, String filterPattern, String target, String filterColRange, String fieldIndex) {
        super(VBS_FILE_PATH);
        params(tabName, filterPattern, target, filterColRange, fieldIndex);
    }

    public Filter tabName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public Filter pattern(String filterPattern) {
        getParameters().set(1, filterPattern);
        return this;
    }

    public Filter target(String target) {
        getParameters().set(2, target);
        return this;
    }

    public Filter colRange(String filterColRange) {
        getParameters().set(3, filterColRange);
        return this;
    }

    public Filter fieldIndex(int index) {
        getParameters().set(4, String.valueOf(index));
        return this;
    }

}
