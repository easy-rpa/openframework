package eu.ibagroup.easyrpa.openframework.excel.style;

import eu.ibagroup.easyrpa.openframework.excel.DataFormat;
import org.apache.poi.ss.usermodel.BuiltinFormats;

public enum DataFormats {

    /**
     * Undefined format
     */
    UNDEFINED(0, -1),

    /**
     * General
     */
    GENERAL(1, 0),

    /**
     * Format string: "0"
     */
    INTEGER(2, 1),

    /**
     * Format string: "0.00"
     */
    FLOAT(3, 2),

    /**
     * Format string: "#,##0"
     */
    SEPARATED_INTEGER(4, 3),

    /**
     * Format string: "#,##0.00"
     */
    SEPARATED_FLOAT(5, 4),

    /**
     * Format string: "\"$\"#,##0_);(\"$\"#,##0)"
     */
    USD_INTEGER(6, 5),

    /**
     * Format string: "\"$\"#,##0_);[Red](\"$\"#,##0)"
     */
    USD_INTEGER_WITH_NEGATIVE_RED(7, 6),

    /**
     * Format string: "\"$\"#,##0.00_);(\"$\"#,##0.00)"
     */
    USD_FLOAT(8, 7),

    /**
     * Format string: "\"$\"#,##0.00_);[Red](\"$\"#,##0.00)"
     */
    USD_FLOAT_WITH_NEGATIVE_RED(9, 8),

    /**
     * Format string: "0%"
     */
    PERCENT_INTEGER(10, 9),

    /**
     * Format string: "0.00%"
     */
    PERCENT_FLOAT(11, 10),

    /**
     * Format string: "0.00E+00"
     */
    SCIENTIFIC_1(12, 11),

    /**
     * Format string: "# ?/?"
     */
    FRACTION_UP_TO_ONE_DIGITS(13, 12),

    /**
     * Format string: "# ??/??"
     */
    FRACTION_UP_TO_TWO_DIGITS(14, 13),

    /**
     * Format string: "m/d/yy"
     */
    MONTH_DAY_YEAR(15, 14),

    /**
     * Format string: "d-mmm-yy"
     */
    DAY_MONTH_YEAR(16, 15),

    /**
     * Format string: "d-mmm"
     */
    DAY_MONTH(17, 16),

    /**
     * Format string: "mmm-yy"
     */
    MONTH_YEAR(18, 17),

    /**
     * Format string: "h:mm AM/PM"
     */
    HOURS_MINUTES_AM_PM(19, 18),

    /**
     * Format string: "h:mm:ss AM/PM"
     */
    HOURS_MINUTES_SECS_AM_PM(20, 19),

    /**
     * Format string: "h:mm"
     */
    HOURS_MINUTES(21, 20),

    /**
     * Format string: "h:mm:ss"
     */
    HOURS_MINUTES_SECS(22, 21),

    /**
     * Format string: "m/d/yy h:mm"
     */
    DATE_TIME(23, 22),

    /**
     * Format string: "#,##0_);(#,##0)"
     */
    INTEGER_WITH_NEGATIVE_IN_BRACKETS(24, 37),

    /**
     * Format string: "#,##0_);[Red](#,##0)"
     */
    INTEGER_WITH_NEGATIVE_IN_BRACKETS_RED(25, 38),

    /**
     * Format string: "#,##0.00_);(#,##0.00)"
     */
    FLOAT_WITH_NEGATIVE_IN_BRACKETS(26, 39),

    /**
     * Format string: "#,##0.00_);[Red](#,##0.00)"
     */
    FLOAT_WITH_NEGATIVE_IN_BRACKETS_RED(27, 40),

    /**
     * Format string: "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)"
     */
    INTEGER_WITH_NEGATIVE_IN_BRACKETS_HYPHENS_FOR_ZEROS(28, 41),

    /**
     * Format string: "_(\"$\"* #,##0_);_(\"$\"* (#,##0);_(\"$\"* \"-\"_);_(@_)"
     */
    USD_INTEGER_WITH_NEGATIVE_IN_BRACKETS_HYPHENS_FOR_ZEROS(29, 42),

    /**
     * Format string: "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)"
     */
    FLOAT_WITH_NEGATIVE_IN_BRACKETS_HYPHENS_FOR_ZEROS(30, 43),

    /**
     * Format string: "_(\"$\"* #,##0.00_);_(\"$\"* (#,##0.00);_(\"$\"* \"-\"??_);_(@_)"
     */
    USD_FLOAT_WITH_NEGATIVE_IN_BRACKETS_HYPHENS_FOR_ZEROS(31, 44),

    /**
     * Format string: "mm:ss"
     */
    MINUTES_SECS(32, 45),

    /**
     * Format string: "[h]:mm:ss"
     */
    HOURS_AMOUNT_MINUTES_SECONDS(33, 46),

    /**
     * Format string: "mm:ss.0"
     */
    MINUTES_SECS_MILLIS(34, 47),

    /**
     * Format string: "##0.0E+0"
     */
    SCIENTIFIC_2(35, 48),

    /**
     * Format string: "@"
     */
    TEXT(36, 49);

    private static final DataFormat[] _formats = new DataFormat[37];

    static {
        for (DataFormats format : values()) {
            _formats[format._idx] = new DataFormat(format.poiIndex, BuiltinFormats.getBuiltinFormat(format.poiIndex));
        }
    }

    private final int _idx;

    private final short poiIndex;

    DataFormats(int _idx, int poiIndex) {
        this._idx = _idx;
        this.poiIndex = (short) poiIndex;
    }

    public DataFormat get() {
        return _formats[_idx];
    }

    public short getPoiIndex() {
        return poiIndex;
    }
}
