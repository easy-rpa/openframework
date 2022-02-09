package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_creating.entities;

import eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_creating.entities.formatters.HeaderFormatter;
import eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_creating.entities.formatters.SurvivedFormatter;
import eu.ibagroup.easyrpa.openframework.google.sheets.annotations.GSheetCellStyle;
import eu.ibagroup.easyrpa.openframework.google.sheets.annotations.GSheetColumn;
import eu.ibagroup.easyrpa.openframework.google.sheets.annotations.GSheetTable;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.BorderStyle;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.Colors;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.HorizontalAlignment;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.VerticalAlignment;
import lombok.Data;

@Data
@GSheetTable(
        headerStyle = @GSheetCellStyle(
                bold = true, background = Colors.DARK_GRAY,
                hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.MIDDLE,
                border = {BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID, BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID}
        ),
        cellStyle = @GSheetCellStyle(vAlign = VerticalAlignment.TOP, border = BorderStyle.SOLID),
        formatter = HeaderFormatter.class
)
public class Passenger {

    @GSheetColumn(name = "Passenger Id", width = 120,
            headerStyle = @GSheetCellStyle(
                    bold = true, background = Colors.DARK_GRAY,
                    hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.MIDDLE,
                    border = {BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID, BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID_MEDIUM}
            ),
            cellStyle = @GSheetCellStyle(
                    vAlign = VerticalAlignment.TOP,
                    border = {BorderStyle.SOLID, BorderStyle.SOLID, BorderStyle.SOLID, BorderStyle.SOLID_MEDIUM}
            )
    )
    private Integer passengerId;

    @GSheetColumn(name = "Name", width = 500)
    private String name;

    @GSheetColumn(name = "Sex", width = 120)
    private String sex;

    @GSheetColumn(name = "Age", width = 120)
    private Integer age;

    @GSheetColumn(name = "Survived", width = 120, formatter = SurvivedFormatter.class)
    private boolean survived;

    @GSheetColumn(name = "Class", width = 120, cellStyle = @GSheetCellStyle(
            hAlign = HorizontalAlignment.CENTER, border = BorderStyle.SOLID
    ))
    private Integer pClass;

    @GSheetColumn(name = "Siblings on board", width = 200)
    private Integer SibSp;

    @GSheetColumn(name = "Parch", width = 120)
    private Integer parch;

    @GSheetColumn(name = "Ticket", width = 120)
    private String ticket;

    @GSheetColumn(name = "Fare", width = 120)
    private Double fare;

    @GSheetColumn(name = "Cabin", width = 120)
    private String cabin;

    @GSheetColumn(name = "Embarked", width = 120,
            headerStyle = @GSheetCellStyle(
                    bold = true, background = Colors.DARK_GRAY,
                    hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.MIDDLE,
                    border = {BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID}
            ),
            cellStyle = @GSheetCellStyle(
                    vAlign = VerticalAlignment.TOP,
                    border = {BorderStyle.SOLID, BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID, BorderStyle.SOLID}
            )
    )
    private String embarked;
}
