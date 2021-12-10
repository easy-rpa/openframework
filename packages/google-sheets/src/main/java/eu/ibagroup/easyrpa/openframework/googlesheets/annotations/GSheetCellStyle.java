package eu.ibagroup.easyrpa.openframework.googlesheets.annotations;

import eu.ibagroup.easyrpa.openframework.googlesheets.GSheetColor;
import eu.ibagroup.easyrpa.openframework.googlesheets.constants.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.style.BorderStyle;
import eu.ibagroup.easyrpa.openframework.googlesheets.style.GSheetColors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSheetCellStyle {
    public static final GSheetColor DEFAULT_BACKGROUND_COLOR = GSheetColors.DEFAULT.get();

    FontFamily fontName() default FontFamily.ARIAL;

    int fontSize() default Font.DEFAULT_FONT_SIZE;

    boolean bold() default false;

    boolean italic() default false;

    boolean strikeout() default false;

    boolean underline() default false;

    GSheetColors color() default GSheetColors.DEFAULT;

    GSheetColors background() default GSheetColors.WHITE;

    HorizontalAlignment hAlign() default HorizontalAlignment.LEFT;

    VerticalAlignment vAlign() default VerticalAlignment.BOTTOM;

    WrapStrategies wrapText() default WrapStrategies.OVERFLOW_CELL;

    int rotation() default 0;

    boolean vertical() default false;

    int topBorderWidth() default 1;

    int rightBorderWidth() default 1;

    int bottomBorderWidth() default 1;

    int leftBorderWidth() default 1;

    String topBorderStyle() default BorderStyle.NONE;

    String rightBorderStyle() default BorderStyle.NONE;

    String bottomBorderStyle() default BorderStyle.NONE;

    String leftBorderStyle() default BorderStyle.NONE;

    GSheetColors topBorderColor() default GSheetColors.BLACK;

    GSheetColors rightBorderColor() default GSheetColors.BLACK;

    GSheetColors bottomBorderColor() default GSheetColors.BLACK;

    GSheetColors leftBorderColor() default GSheetColors.BLACK;

    int topPadding() default 0;

    int bottomPadding() default 0;

    int leftPadding() default 0;

    int rightPadding() default 0;

    TextDirections textDirection() default TextDirections.LEFT_TO_RIGHT;

}