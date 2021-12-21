package eu.ibagroup.easyrpa.openframework.excel.style;

/**
 * Represents specific data format for numeric value of cell.
 * <p><br>
 * Format - is a string that consists of 4 sections, separated by semicolons, in this order:
 * <pre>
 * POSITIVE; NEGATIVE; ZERO; TEXT
 * </pre>
 * Here's an example:
 * <pre>
 *    1          2      3      4
 * #,##0.00;(#,##0.00);"-";[Magenta]@
 * </pre>
 * <ol>
 * <li>Format for positive numbers (display 2 decimal places and a thousands separator).
 * <li>Format for negative numbers (the same as for positive numbers, but enclosed in parenthesis).
 * <li>Format for zeros (display dashes instead of zeros).
 * <li>Format for text values (display text in magenta font color).
 * </ol>
 *
 * <p>Placeholders explanation:</p>
 * <table style="vertical-align:top">
 *     <tr><th style="padding-right:10px">Code</th><th style="padding-right:10px">Description</th><th>Example</th></tr>
 *     <tr><td>0</td><td>Digit placeholder that displays insignificant zeros.</td><td>#.00 - always
 *     displays 2 decimal places. If you type 5.5 in a cell, it will display as 5.50.</td></tr>
 *     <tr><td>#</td><td>Digit placeholder that represents optional digits and does not display extra zeros. That is,
 *     if a number doesn't need a certain digit, it won't be displayed.</td><td>#.## - displays up to 2 decimal places.
 *     If you type 5.5 in a cell, it will display as 5.5. If you type 5.555, it will display as 5.56.</td></tr>
 *     <tr><td>?</td><td>Digit placeholder that leaves a space for insignificant zeros on either side of the decimal
 *     point but doesn't display them. It is often used to align numbers in a column by decimal point.</td><td>#.??? -
 *     displays a maximum of 3 decimal places and aligns numbers in a column by decimal point.</td></tr>
 *     <tr><td>@</td><td>Text placeholder</td><td>0.00; -0.00; 0; [Red]@ - applies the red font color for text values.</td></tr>
 * </table>
 * <p>
 * For more info see <a href="https://www.ablebits.com/office-addins-blog/2016/07/07/custom-excel-number-format/">
 *     https://www.ablebits.com/office-addins-blog/2016/07/07/custom-excel-number-format/</a>
 */
public class DataFormat {

    /**
     * Index of related built-in Excel format.
     */
    private short index = -1;

    /**
     * Actual format string
     */
    private String format;

    /**
     * Creates instance of data format for indexed built-in format.
     *
     * @param index  built-in format index.
     * @param format built-in format string.
     */
    public DataFormat(short index, String format) {
        this.index = index;
        this.format = format;
    }

    /**
     * Creates instance of data format for custom format.
     *
     * @param format custom format string
     */
    public DataFormat(String format) {
        this.format = format;
    }

    /**
     * Gets index of related built-in Excel format.
     *
     * @return index of related built-in Excel format or <code>-1</code> if format is custom.
     */
    public short getIndex() {
        return index;
    }

    /**
     * Gets an actual format string.
     *
     * @return format string.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Checks whether the current data format is built-in Excel format.
     *
     * @return <code>true</code> if the current data format is built-in Excel format or <code>false</code> otherwise.
     */
    public boolean isIndexed() {
        return index >= 0;
    }

    /**
     * Checks whether the current data format is properly defined.
     *
     * @return <code>true</code> if the current data format is defined and has corresponding format string or
     * <code>false</code> otherwise.
     */
    public boolean isDefined() {
        return index >= 0 || format != null;
    }
}
