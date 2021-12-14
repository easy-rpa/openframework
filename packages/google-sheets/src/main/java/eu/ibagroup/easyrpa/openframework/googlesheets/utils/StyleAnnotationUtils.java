package eu.ibagroup.easyrpa.openframework.googlesheets.utils;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetCellStyle;

public class StyleAnnotationUtils {
    public static TextRotation getRotation(GSheetCellStyle styleAnnotation){
        TextRotation rotation = new TextRotation();
        if(styleAnnotation.rotation() != 0){
            rotation.setAngle(styleAnnotation.rotation());
        }
        if(styleAnnotation.vertical()){
            rotation.setVertical(true);
        }
        return rotation;
    }

    public static Color getBackgroundColor(GSheetCellStyle styleAnnotation){
        return styleAnnotation.background().get().toNativeColor();
    }


    public static TextFormat getTextFormat(GSheetCellStyle styleAnnotation) {
        TextFormat format = new TextFormat();
        format.setItalic(styleAnnotation.italic());
        format.setFontFamily(styleAnnotation.fontName().name());
        format.setUnderline(styleAnnotation.underline())   ;
        format.setStrikethrough(styleAnnotation.strikeout());
        format.setBold(styleAnnotation.bold());
        format.setFontSize(styleAnnotation.fontSize());
        format.setForegroundColor(styleAnnotation.color().get().toNativeColor());
        return format;
    }

    public static Borders getBorders(GSheetCellStyle styleAnnotation) {
        Borders borders = new Borders();

        Border topBorder = new Border();
        topBorder.setStyle(styleAnnotation.topBorderStyle());
        topBorder.setColor(styleAnnotation.topBorderColor().get().toNativeColor());
        topBorder.setWidth(styleAnnotation.topBorderWidth());

        Border bottomBorder = new Border();
        bottomBorder.setStyle(styleAnnotation.bottomBorderStyle());
        bottomBorder.setColor(styleAnnotation.bottomBorderColor().get().toNativeColor());
        bottomBorder.setWidth(styleAnnotation.bottomBorderWidth());

        Border leftBorder = new Border();
        leftBorder.setStyle(styleAnnotation.leftBorderStyle());
        leftBorder.setColor(styleAnnotation.leftBorderColor().get().toNativeColor());
        leftBorder.setWidth(styleAnnotation.leftBorderWidth());

        Border rightBorder = new Border();
        rightBorder.setStyle(styleAnnotation.rightBorderStyle());
        rightBorder.setColor(styleAnnotation.rightBorderColor().get().toNativeColor());
        rightBorder.setWidth(styleAnnotation.rightBorderWidth());

        borders.setTop(topBorder);
        borders.setBottom(bottomBorder);
        borders.setLeft(leftBorder);
        borders.setRight(rightBorder);
        return borders;
    }

    public static String getHorizontalAlignment(GSheetCellStyle styleAnnotation) {
        return styleAnnotation.hAlign().name();
    }

    public static String getVerticalAlignment(GSheetCellStyle styleAnnotation) {
        return styleAnnotation.vAlign().name();
    }

    public static String getWrapStrategy(GSheetCellStyle styleAnnotation) {
        return styleAnnotation.wrapText().name();
    }

    public static Padding getPadding(GSheetCellStyle styleAnnotation) {
        Padding padding = new Padding();
        padding.setTop(styleAnnotation.topPadding());
        padding.setBottom(styleAnnotation.bottomPadding());
        padding.setRight(styleAnnotation.rightPadding());
        padding.setLeft(styleAnnotation.leftPadding());
        return padding;
    }

    public static String getTextDirection(GSheetCellStyle styleAnnotation) {
        return styleAnnotation.textDirection().toString();
    }
}
