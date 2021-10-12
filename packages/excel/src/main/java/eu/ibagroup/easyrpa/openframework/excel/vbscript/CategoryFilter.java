package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * Example of script execution : 'cscript "C:/scripts/filter.vbs"
 * "C:/Users/user1/AppData/Local/Temp/User Report.xlsx" "Sheet0" "Active"
 * "F1:F183" "1"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: Sheet name (Tab name) Argument
 * 2: category (default 'Active') Argument 3: Range Argument 4: fieldIndex 'The
 * integer offset of the field on which you want to base the filter (from the
 * left of the list; the leftmost field is field one).
 */
public class CategoryFilter extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/categoryFilter.vbs";

    /**
     * Construct empty CategoryFilter. Methods to add parameters must be used before
     * perform();
     */
    public CategoryFilter() {
        this("", "", "", "");
    }

    public CategoryFilter(String tabName, String filterColRange) {
        this(tabName, "Active", filterColRange, "1");
    }

    /**
     * Apply filter to column with fieldIndex
     *
     * @param tabName        -
     * @param category       the value of cell. This row will in the result table if
     *                       cell contains this value
     * @param filterColRange -
     * @param fieldIndex     -
     */
    public CategoryFilter(String tabName, String category, String filterColRange, String fieldIndex) {
        super(VBS_FILE_PATH);
        params(tabName, category, filterColRange, fieldIndex);
    }

    public CategoryFilter tabName(String tabName) {
        getParameters().set(0, tabName);
        return this;
    }

    public CategoryFilter category(String category) {
        getParameters().set(1, category);
        return this;
    }

    public CategoryFilter colRange(String filterColRange) {
        getParameters().set(2, filterColRange);
        return this;
    }

    public CategoryFilter fieldIndex(int index) {
        getParameters().set(3, String.valueOf(index));
        return this;
    }

}
