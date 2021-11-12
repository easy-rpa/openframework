package eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities;

import eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities.formatters.SurvivedFormatter;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelCellStyle;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelColumn;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelTable;
import eu.ibagroup.easyrpa.openframework.excel.style.Fonts;
import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;

@Data
@ExcelTable(headerStyle = @ExcelCellStyle(font = Fonts.BOLD ^ Fonts.ITALIC, background = IndexedColors.GREY_50_PERCENT))
public class Passenger {

    @ExcelColumn(name = "Passenger Id")
    private Integer passengerId;

    @ExcelColumn(name = {"Person Info", "Name"})
    private String name;

    @ExcelColumn(name = {"Person Info", "Sex"})
    private String sex;

    @ExcelColumn(name = {"Person Info", "Age"})
    private Integer age;

    @ExcelColumn(name = "Survived", formatter = SurvivedFormatter.class)
    private boolean survived;

    @ExcelColumn(name = "Class", cellStyle = {
            @ExcelCellStyle(background = IndexedColors.BRIGHT_GREEN)
    })
    private Integer pClass;

    @ExcelColumn(name = "Siblings on board")
    private Integer SibSp;

    @ExcelColumn(name = "Parch")
    private Integer parch;

    @ExcelColumn(name = "Ticket")
    private String ticket;

    @ExcelColumn(name = "Fare")
    private Double fare;

    @ExcelColumn(name = "Cabin")
    private String cabin;

    @ExcelColumn(name = "Embarked")
    private String embarked;
}
