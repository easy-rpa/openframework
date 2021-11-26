package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*@ExcelTable(
        headerStyle = @ExcelCellStyle(
                bold = true, background = ExcelColors.GREY_25_PERCENT,
                hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.CENTER,
                topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
                bottomBorder = BorderStyle.MEDIUM, leftBorder = BorderStyle.THIN
        ),
        cellStyle = @ExcelCellStyle(
                vAlign = VerticalAlignment.TOP,
                topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
                bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
        ),
        formatter = HeaderFormatter.class
)*/
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
    private Integer passengerId;

//    @ExcelColumn(name = "Name", width = 50)
    private String name;

//    @ExcelColumn(name = "Sex", width = 12)
    private String sex;

//    @ExcelColumn(name = "Age", width = 12)
    private Integer age;

//    @ExcelColumn(name = "Survived", width = 12, formatter = SurvivedFormatter.class)
    private boolean survived;

//    @ExcelColumn(name = "Class", width = 12, cellStyle = @ExcelCellStyle(
//            hAlign = HorizontalAlignment.CENTER,
//            topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
//            bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
//    ))
    private Integer pClass;

//    @ExcelColumn(name = "Siblings on board", width = 20)
    private Integer SibSp;

//    @ExcelColumn(name = "Parch", width = 12)
    private Integer parch;

//    @ExcelColumn(name = "Ticket", width = 12)
    private String ticket;

//    @ExcelColumn(name = "Fare", width = 12)
    private Double fare;

//    @ExcelColumn(name = "Cabin", width = 12)
    private String cabin;

//    @ExcelColumn(name = "Embarked", width = 12,
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
