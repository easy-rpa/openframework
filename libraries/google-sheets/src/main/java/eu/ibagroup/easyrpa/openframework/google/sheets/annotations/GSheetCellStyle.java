package eu.ibagroup.easyrpa.openframework.google.sheets.annotations;

import eu.ibagroup.easyrpa.openframework.google.sheets.constants.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines parameters of style that should be applied to specific cells of Google Spreadsheet document.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSheetCellStyle {

    /**
     * Specifies the font of cell to apply.
     */
    FontFamily font() default FontFamily.UNSPECIFIED;

    /**
     * Specifies size of cell font in points.
     */
    int fontSize() default 0;

    /**
     * Specifies whether cell font should be bold.
     * <br>
     * Default value: {@code false}
     */
    boolean bold() default false;

    /**
     * Specifies whether cell font should be italic.
     * <br>
     * Default value: {@code false}
     */
    boolean italic() default false;

    /**
     * Specifies whether cell font should be strikeout.
     * <br>
     * Default value: {@code false}
     */
    boolean strikeout() default false;

    /**
     * Specifies whether cell font should be underlined.
     * <br>
     * Default value: {@code false}
     */
    boolean underline() default false;

    /**
     * Specifies color of cell font.
     *
     * @see Colors
     */
    Colors color() default Colors.UNSPECIFIED;

    /**
     * Specifies number format of cell.
     *
     * @see NumberFormats
     */
    NumberFormats numberFormat() default NumberFormats.UNSPECIFIED;

    /**
     * Specifies custom number format of cell using pattern string.
     * <p>
     * See the <a href="https://developers.google.cn/sheets/api/guides/formats?hl=id">Date and Number Formats guide</a>
     * for more information about the supported patterns.
     */
    String numberPattern() default "";

    /**
     * Specifies background color of cell.
     *
     * @see Colors
     */
    Colors background() default Colors.UNSPECIFIED;

    /**
     * Specifies horizontal alignment of text in the cell.
     *
     * @see HorizontalAlignment
     */
    HorizontalAlignment hAlign() default HorizontalAlignment.UNSPECIFIED;

    /**
     * Specifies vertical alignment of text in the cell.
     *
     * @see VerticalAlignment
     */
    VerticalAlignment vAlign() default VerticalAlignment.UNSPECIFIED;

    /**
     * Specifies how text in the cell should wrap.
     *
     * @see WrapStrategy
     */
    WrapStrategy wrapStrategy() default WrapStrategy.UNSPECIFIED;

    /**
     * Specifies degree of rotation for the text in the cell.
     */
    int rotation() default 0;

    /**
     * Specifies whether the text in the cell should be displayed vertically.
     * <br>
     * Default value: {@code false}
     */
    boolean vertical() default false;

    /**
     * Specifies type of borders to use for the cell.
     * <p>
     * It accepts single value (one style for all borders of the cell) or an array with 4 values where each value
     * correspond to specific border in the following order:
     * <pre>
     *     {@code [<top>, <right>, <bottom>, <left>]}
     * </pre>
     *
     * @see BorderStyle
     */
    BorderStyle[] border() default {};

    /**
     * Specifies color of borders to use for the cell.
     * <p>
     * It accepts single value (one color for all borders of the cell) or an array with 4 values where each value
     * correspond to specific border in the following order:
     * <pre>
     *     {@code [<top>, <right>, <bottom>, <left>]}
     * </pre>
     *
     * @see Colors
     */
    Colors[] borderColor() default {};

    /**
     * Specifies the amount of padding around the cell, in pixels.
     * <p>
     * It accepts single value (one amount for all sides of the cell) or an array with 4 values where each value
     * correspond to amount of padding for specific side in the following order:
     * <pre>
     *     {@code [<top>, <right>, <bottom>, <left>]}
     * </pre>
     */
    int[] padding() default {};
}