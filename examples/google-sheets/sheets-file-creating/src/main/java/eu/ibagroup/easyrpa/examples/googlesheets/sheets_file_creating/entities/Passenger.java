package eu.ibagroup.easyrpa.examples.googlesheets.sheets_file_creating.entities;

import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetCellStyle;
import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetColumn;
import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetTable;
import eu.ibagroup.easyrpa.openframework.googlesheets.constants.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.style.BorderStyle;
import eu.ibagroup.easyrpa.openframework.googlesheets.style.GSheetColors;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@GSheetTable(
        headerStyle = @GSheetCellStyle(
                bold = true, background = GSheetColors.GRAY,
                fontSize = 16, fontName = FontFamily.COURIER_NEW,
                topBorderColor = GSheetColors.BLACK, topBorderStyle = BorderStyle.SOLID, topBorderWidth = 3,
                bottomBorderColor = GSheetColors.BLACK, bottomBorderStyle = BorderStyle.SOLID, bottomBorderWidth = 3,
                leftBorderColor = GSheetColors.BLACK, leftBorderStyle = BorderStyle.SOLID, leftBorderWidth = 3,
                rightBorderColor = GSheetColors.BLACK, rightBorderStyle = BorderStyle.SOLID, rightBorderWidth = 3,
                vAlign = VerticalAlignment.MIDDLE, hAlign = HorizontalAlignment.RIGHT, wrapText = WrapStrategies.LEGACY_WRAP,
                bottomPadding = 20, topPadding = 20, rightPadding = 20, leftPadding = 20
        ),
        cellStyle = @GSheetCellStyle(
                background = GSheetColors.LIGHT_GRAY, color = GSheetColors.ORANGE, strikeout = true, fontSize = 14,
//                topBorderColor = GSheetColors.ORANGE, topBorderStyle = BorderStyle.DOUBLE, topBorderWidth = 2,
                bottomBorderColor = GSheetColors.ORANGE, bottomBorderStyle = BorderStyle.DOUBLE, bottomBorderWidth = 2,
                leftBorderColor = GSheetColors.ORANGE, leftBorderStyle = BorderStyle.DOUBLE, leftBorderWidth = 2,
                rightBorderColor = GSheetColors.ORANGE, rightBorderStyle = BorderStyle.DOUBLE, rightBorderWidth = 2,
                vAlign = VerticalAlignment.BOTTOM, hAlign = HorizontalAlignment.CENTER, wrapText = WrapStrategies.LEGACY_WRAP,
                textDirection = TextDirections.RIGHT_TO_LEFT
        )//,
        //formatter = HeaderFormatter.class
)
@Data
public class Passenger {

    /*    @ExcelColumn(name = "Passenger Id", width = 12,
                headerStyle = @ExcelCellStyle(
                        bold = true, background = ExcelColors.GREY_25_PERCENT,
                        hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.CENTER,
                        topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
                        bottomBorder = BorderStyle.MEDIUM, leftBorder = BorderStyle.MEDIUM
                ),
                cellStyle = @ExcelCellStyle(
                        vAlign = VerticalAlignment.TOP,
                        topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
                        bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.MEDIUM
                )
        )*/
    @GSheetColumn(name = "Passenger Id", width = 19)
    private Integer passengerId;

    @GSheetColumn(name = "Name")
    private String name;

    @GSheetColumn(name = "Sex")
    private String sex;

    @GSheetColumn(name = "Age")
    private Integer age;

    @GSheetColumn(name = "Survived")
    private boolean survived;

    //    @ExcelColumn(name = "Class", width = 12, cellStyle = @ExcelCellStyle(
//            hAlign = HorizontalAlignment.CENTER,
//            topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
//            bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
//    ))
    @GSheetColumn(name = "Class")
    private Integer pClass;

    @GSheetColumn(name = "Siblings on board", width = 20)
    private Integer SibSp;

    @GSheetColumn(name = "Parch", width = 12)
    private Integer parch;

    @GSheetColumn(name = "Ticket", width = 12)
    private String ticket;

    @GSheetColumn(name = "Fare", width = 12)
    private Double fare;

    @GSheetColumn(name = "Cabin", width = 12)
    private String cabin;

    @GSheetColumn(name = "Embarked", width = 12)
//            headerStyle = @ExcelCellStyle(
//                    bold = true, background = ExcelColors.GREY_25_PERCENT,
//                    hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.CENTER,
//                    topBorder = BorderStyle.THIN, rightBorder = BorderStyle.MEDIUM,
//                    bottomBorder = BorderStyle.MEDIUM, leftBorder = BorderStyle.THIN
//            ),
//            cellStyle = @ExcelCellStyle(
//                    vAlign = VerticalAlignment.TOP,
//                    topBorder = BorderStyle.THIN, rightBorder = BorderStyle.MEDIUM,
//                    bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
//            )
//    )
    private String embarked;

    public List<Object> toObjectList(){
        List<Object> res = new ArrayList();
        res.add(getValue(getPassengerId()));
        res.add(getValue(getName()));
        res.add(getValue(getSex()));
        res.add(getValue(getAge()));
        res.add(getValue(isSurvived()));
        res.add(getValue(getPClass()));
        res.add(getValue(getSibSp()));
        res.add(getValue(getParch()));
        res.add(getValue(getTicket()));
        res.add(getValue(getFare()));
        res.add(getValue(getCabin()));
        return res;
    }
    Object getValue(Object obj){
        if(obj == null){
            return "";
        }
        return obj;
    }
}
