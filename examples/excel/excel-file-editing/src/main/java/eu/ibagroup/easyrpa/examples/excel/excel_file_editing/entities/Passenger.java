package eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities;

import eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.formatters.AgeFormatter;
import eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.formatters.HeaderFormatter;
import eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.formatters.SurvivedFormatter;
import eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.mappers.SurvivedFieldMapper;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelCellStyle;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelColumn;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ExcelTable(
        cellStyle = @ExcelCellStyle(
                vAlign = VerticalAlignment.TOP,
                topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
                bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
        ),
        formatter = HeaderFormatter.class
)
public class Passenger {

    @EqualsAndHashCode.Include
    @ExcelColumn(name = "Passenger Id", cellStyle = @ExcelCellStyle(
            vAlign = VerticalAlignment.TOP,
            topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
            bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.MEDIUM
    ))
    private Integer passengerId;

    @ExcelColumn(name = "Name")
    private String name;

    @ExcelColumn(name = "Sex")
    private String sex;

    @ExcelColumn(name = "Age", formatter = AgeFormatter.class)
    private Integer age;

    @ExcelColumn(name = "Survived", mapper = SurvivedFieldMapper.class, formatter = SurvivedFormatter.class)
    private boolean survived;

    @ExcelColumn(name = "Class", cellStyle = @ExcelCellStyle(
            hAlign = HorizontalAlignment.CENTER,
            topBorder = BorderStyle.THIN, rightBorder = BorderStyle.THIN,
            bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
    ))
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

    @ExcelColumn(name = "Embarked", cellStyle = @ExcelCellStyle(
            vAlign = VerticalAlignment.TOP,
            topBorder = BorderStyle.THIN, rightBorder = BorderStyle.MEDIUM,
            bottomBorder = BorderStyle.THIN, leftBorder = BorderStyle.THIN
    ))
    private String embarked;
}
