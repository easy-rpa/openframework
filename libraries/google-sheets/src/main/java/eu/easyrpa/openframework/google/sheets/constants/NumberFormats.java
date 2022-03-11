package eu.easyrpa.openframework.google.sheets.constants;

/**
 * Helps to identify required number format for specific cell of Google Spreadsheet document.
 */
public enum NumberFormats {

    /**
     * Unspecified format
     */
    UNSPECIFIED,

    /**
     * Text formatting. E.g. 1000.12. The actual format depends on the locale of the spreadsheet.
     */
    TEXT,

    /**
     * Number formatting. E.g. 1,000.12. The actual format depends on the locale of the spreadsheet.
     */
    NUMBER,

    /**
     * Percent formatting. E.g. 10.12%.
     */
    PERCENT,

    /**
     * Currency formatting. E.g. $1,000.12. The actual format depends on the locale of the spreadsheet.
     */
    CURRENCY,

    /**
     * Date formatting. E.g. 9/26/2008. The actual format depends on the locale of the spreadsheet.
     */
    DATE,

    /**
     * Time formatting. E.g. 3:59:00 PM. The actual format depends on the locale of the spreadsheet.
     */
    TIME,

    /**
     * Date+Time formatting. E.g. 9/26/08 15:59:00. The actual format depends on the locale of the spreadsheet.
     */
    DATE_TIME,

    /**
     * Scientific number formatting. E.g. 1.01E+03.
     */
    SCIENTIFIC;
}
