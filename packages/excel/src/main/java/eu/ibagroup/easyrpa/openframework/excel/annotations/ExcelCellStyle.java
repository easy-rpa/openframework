package eu.ibagroup.easyrpa.openframework.excel.annotations;

import eu.ibagroup.easyrpa.openframework.excel.style.DataFormats;
import eu.ibagroup.easyrpa.openframework.excel.style.ExcelColors;
import eu.ibagroup.easyrpa.openframework.excel.style.FontOffsetType;
import eu.ibagroup.easyrpa.openframework.excel.style.FontUnderlineStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines parameters of style that should be applied to specific cells of Excel Document.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCellStyle {

    /**
     * Specifies name of cell font to apply.
     * <br>
     * Default value: {@link XSSFFont#DEFAULT_FONT_NAME}
     */
    String fontName() default XSSFFont.DEFAULT_FONT_NAME;

    /**
     * Specifies size of cell font in points.
     * <br>
     * Default value: {@link XSSFFont#DEFAULT_FONT_SIZE}
     */
    short fontSize() default XSSFFont.DEFAULT_FONT_SIZE;

    /**
     * Specifies whether cell font should be bold.
     * <br>
     * Default value: <code>false</code>
     */
    boolean bold() default false;

    /**
     * Specifies whether cell font should be italic.
     * <br>
     * Default value: <code>false</code>
     */
    boolean italic() default false;

    /**
     * Specifies whether cell font should be strikeout.
     * <br>
     * Default value: <code>false</code>
     */
    boolean strikeout() default false;

    /**
     * Specifies whether cell font should be underlined and defines its style.
     * <br>
     * Default value: {@link FontUnderlineStyle#NONE}
     *
     * @see FontUnderlineStyle
     */
    FontUnderlineStyle underline() default FontUnderlineStyle.NONE;

    /**
     * Specifies offset type of cell font (normal, superscript or subscript).
     * <br>
     * Default value: {@link FontOffsetType#NORMAL}
     *
     * @see FontOffsetType
     */
    FontOffsetType fontOffset() default FontOffsetType.NORMAL;

    /**
     * Specifies color of cell font.
     * <br>
     * Default value: {@link ExcelColors#AUTOMATIC}
     *
     * @see ExcelColors
     */
    ExcelColors color() default ExcelColors.AUTOMATIC;

    /**
     * Specifies cell data format.
     * <br>
     * Default value: {@link DataFormats#GENERAL}
     *
     * @see DataFormats
     */
    DataFormats dataFormat() default DataFormats.GENERAL;

    /**
     * Specifies background color of cell.
     * <br>
     * Default value: {@link ExcelColors#AUTOMATIC}
     *
     * @see ExcelColors
     */
    ExcelColors background() default ExcelColors.AUTOMATIC;

    /**
     * Specifies type of filling background with color.
     * <br>
     * Default value: {@link FillPatternType#SOLID_FOREGROUND} if background color is specified and
     * {@link FillPatternType#NO_FILL} otherwise.
     *
     * @see FillPatternType
     */
    FillPatternType fill() default FillPatternType.NO_FILL;

    /**
     * Specifies horizontal alignment of text in the cell.
     * <br>
     * Default value: {@link HorizontalAlignment#GENERAL}
     *
     * @see HorizontalAlignment
     */
    HorizontalAlignment hAlign() default HorizontalAlignment.GENERAL;

    /**
     * Specifies vertical alignment of text in the cell.
     * <br>
     * Default value: {@link VerticalAlignment#BOTTOM}
     *
     * @see VerticalAlignment
     */
    VerticalAlignment vAlign() default VerticalAlignment.BOTTOM;

    /**
     * Specifies whether long-text in the cell should wrap into multiple lines.
     * <br>
     * Default value: <code>false</code>
     */
    boolean wrapText() default false;

    /**
     * Specifies degree of rotation for the text in the cell.
     * <br>
     * Default value: <code>0</code>
     */
    short rotation() default 0;

    /**
     * Specifies type of border to use for the top border of the cell.
     * <br>
     * Default value: {@link BorderStyle#NONE}
     *
     * @see BorderStyle
     */
    BorderStyle topBorder() default BorderStyle.NONE;

    /**
     * Specifies type of border to use for the right border of the cell.
     * <br>
     * Default value: {@link BorderStyle#NONE}
     *
     * @see BorderStyle
     */
    BorderStyle rightBorder() default BorderStyle.NONE;

    /**
     * Specifies type of border to use for the bottom border of the cell.
     * <br>
     * Default value: {@link BorderStyle#NONE}
     *
     * @see BorderStyle
     */
    BorderStyle bottomBorder() default BorderStyle.NONE;

    /**
     * Specifies type of border to use for the left border of the cell.
     * <br>
     * Default value: {@link BorderStyle#NONE}
     *
     * @see BorderStyle
     */
    BorderStyle leftBorder() default BorderStyle.NONE;

    /**
     * Specifies color to use for the top border of the cell.
     * <br>
     * Default value: {@link ExcelColors#BLACK}
     *
     * @see ExcelColors
     */
    ExcelColors topBorderColor() default ExcelColors.BLACK;

    /**
     * Specifies color to use for the right border of the cell.
     * <br>
     * Default value: {@link ExcelColors#BLACK}
     *
     * @see ExcelColors
     */
    ExcelColors rightBorderColor() default ExcelColors.BLACK;

    /**
     * Specifies color to use for the bottom border of the cell.
     * <br>
     * Default value: {@link ExcelColors#BLACK}
     *
     * @see ExcelColors
     */
    ExcelColors bottomBorderColor() default ExcelColors.BLACK;

    /**
     * Specifies color to use for the left border of the cell.
     * <br>
     * Default value: {@link ExcelColors#BLACK}
     *
     * @see ExcelColors
     */
    ExcelColors leftBorderColor() default ExcelColors.BLACK;

    /**
     * Specifies whether the cell should be hidden.
     * <br>
     * Default value: <code>false</code>
     */
    boolean hidden() default false;

    /**
     * Specifies whether the cell should be locked.
     * <br>
     * Default value: <code>false</code>
     */
    boolean locked() default false;

    /**
     * Specifies the number of spaces to indent the text in the cell.
     * <br>
     * Default value: <code>0</code>
     */
    short indention() default 0;
}