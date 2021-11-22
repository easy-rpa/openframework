package eu.ibagroup.easyrpa.openframework.excel;

/**
 * <b>Understanding Excel number format</b>
 * <p>
 * <p>
 * To be able to create a custom format in Excel, it is important that you understand how Microsoft Excel sees the number format. *
 * An Excel number format consists of 4 sections of code, separated by semicolons, in this order:
 * <p>
 * POSITIVE; NEGATIVE; ZERO; TEXT
 * <p>
 * Here's an example of a custom Excel format code:
 * <p>
 * An example of a custom Excel format code
 * <p>
 * 1          2      3      4
 * #,##0.00;(#,##0.00);"-";[Magenta]@
 * <p>
 * 1. Format for positive numbers (display 2 decimal places and a thousands separator).
 * 2. Format for negative numbers (the same as for positive numbers, but enclosed in parenthesis).
 * 3. Format for zeros (display dashes instead of zeros).
 * 4. Format for text values (display text in magenta font color).
 *
 *
 *
 * <b>Excel formatting rules</b>
 * <p>
 * When creating a custom number format in Excel, please remember these rules:
 * <p>
 * A custom Excel number format changes only the visual representation, i.e. how a value is displayed in a cell. The underlying value stored in a cell is not changed.
 * When you are customizing a built-in Excel format, a copy of that format is created. The original number format cannot be changed or deleted.
 * Excel custom number format does not have to include all four sections.
 * <p>
 * If a custom format contains just 1 section, that format will be applied to all number types - positive, negative and zeros.
 * <p>
 * If a custom number format includes 2 sections, the first section is used for positive numbers and zeros, and the second section - for negative numbers.
 * <p>
 * A custom format is applied to text values only if it contains all four sections.
 * To apply the default Excel number format for any of the middle sections, type General instead of the corresponding format code.
 * <p>
 * For example, to display zeros as dashes and show all other values with the default formatting, use this format code: General; -General; "-"; General
 * Note. The General format included in the 2nd section of the format code does not display the minus sign, therefore we include it in the format code.
 * To hide a certain value type(s), skip the corresponding code section, and only type the ending semicolon.
 * <p>
 * For example, to hide zeros and negative values, use the following format code: General; ; ; General. As the result, zeros and negative value will appear only in the formula bar, but will not be visible in cells.
 * To delete a custom number format, open the Format Cells dialog, select Custom in the Category list, find the format you want to delete in the Type list, and click the Delete button.
 *
 *
 * <b>Digit and text placeholders</b>
 * <p>
 * For starters, let's learn 4 basic placeholders that you can use in your custom Excel format.
 * Code | Description | Example
 * 0 |	Digit placeholder that displays insignificant zeros. |	#.00 - always displays 2 decimal places. If you type 5.5 in a cell, it will display as 5.50.
 * # |	Digit placeholder that represents optional digits and does not display extra zeros. That is, if a number doesn't need a certain digit, it won't be displayed. | #.## - displays up to 2 decimal places. If you type 5.5 in a cell, it will display as 5.5. If you type 5.555, it will display as 5.56.
 * ? | Digit placeholder that leaves a space for insignificant zeros on either side of the decimal point but doesn't display them. It is often used to align numbers in a column by decimal point. | #.??? - displays a maximum of 3 decimal places and aligns numbers in a column by decimal point.
 *
 * @ | Text placeholder | 0.00; -0.00; 0; [Red]@ - applies the red font color for text values.
 * <p>
 * <p>
 * For more info: https://www.ablebits.com/office-addins-blog/2016/07/07/custom-excel-number-format/
 */
public class DataFormat {

    private short index = -1;

    private String format;

    public DataFormat(short index, String format) {
        this.index = index;
        this.format = format;
    }

    public DataFormat(String format) {
        this.format = format;
    }

    public short getIndex() {
        return index;
    }

    public String getFormat() {
        return format;
    }

    public boolean isIndexed() {
        return index >= 0;
    }

    public boolean isDefined() {
        return index >= 0 || format != null;
    }
}
