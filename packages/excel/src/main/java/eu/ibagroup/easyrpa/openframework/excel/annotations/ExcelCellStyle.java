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

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCellStyle {

    String fontName() default XSSFFont.DEFAULT_FONT_NAME;

    short fontSize() default XSSFFont.DEFAULT_FONT_SIZE;

    boolean bold() default false;

    boolean italic() default false;

    boolean strikeout() default false;

    FontUnderlineStyle underline() default FontUnderlineStyle.NONE;

    FontOffsetType fontOffset() default FontOffsetType.NORMAL;

    ExcelColors color() default ExcelColors.AUTOMATIC;

    DataFormats dataFormat() default DataFormats.GENERAL;

    ExcelColors background() default ExcelColors.AUTOMATIC;

    FillPatternType fill() default FillPatternType.NO_FILL;

    HorizontalAlignment hAlign() default HorizontalAlignment.GENERAL;

    VerticalAlignment vAlign() default VerticalAlignment.BOTTOM;

    boolean wrapText() default false;

    short rotation() default 0;

    BorderStyle topBorder() default BorderStyle.NONE;

    BorderStyle rightBorder() default BorderStyle.NONE;

    BorderStyle bottomBorder() default BorderStyle.NONE;

    BorderStyle leftBorder() default BorderStyle.NONE;

    ExcelColors topBorderColor() default ExcelColors.BLACK;

    ExcelColors rightBorderColor() default ExcelColors.BLACK;

    ExcelColors bottomBorderColor() default ExcelColors.BLACK;

    ExcelColors leftBorderColor() default ExcelColors.BLACK;

    boolean hidden() default false;

    boolean locked() default false;

    short indention() default 0;
}