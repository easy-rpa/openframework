package eu.ibagroup.easyrpa.openframework.excel.annotations;

import org.apache.poi.ss.usermodel.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCellStyle {

    short font() default -1;

    IndexedColors color() default IndexedColors.BLACK1;

    IndexedColors background() default IndexedColors.BLACK1;

    FillPatternType fill() default FillPatternType.NO_FILL;

    short dataFormat() default -1;

    HorizontalAlignment hAlign() default HorizontalAlignment.LEFT;

    VerticalAlignment vAlign() default VerticalAlignment.TOP;

    boolean wrapText() default false;

    short rotation() default -1;

    BorderStyle borders() default BorderStyle.NONE;

    BorderStyle leftBorder() default BorderStyle.NONE;

    BorderStyle rightBorder() default BorderStyle.NONE;

    BorderStyle topBorder() default BorderStyle.NONE;

    BorderStyle bottomBorder() default BorderStyle.NONE;

    short bordersColor() default -1;

    short leftBorderColor() default -1;

    short rightBorderColor() default -1;

    short topBorderColor() default -1;

    short bottomBorderColor() default -1;
}