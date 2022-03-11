package eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities;

import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters.AgeFormatter;
import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters.HeaderFormatter;
import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters.SurvivedFormatter;
import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.mappers.SurvivedFieldMapper;
import eu.easyrpa.openframework.google.sheets.annotations.GSheetCellStyle;
import eu.easyrpa.openframework.google.sheets.annotations.GSheetColumn;
import eu.easyrpa.openframework.google.sheets.annotations.GSheetTable;
import eu.easyrpa.openframework.google.sheets.constants.BorderStyle;
import eu.easyrpa.openframework.google.sheets.constants.HorizontalAlignment;
import eu.easyrpa.openframework.google.sheets.constants.VerticalAlignment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@GSheetTable(
        cellStyle = @GSheetCellStyle(vAlign = VerticalAlignment.TOP, border = BorderStyle.SOLID),
        formatter = HeaderFormatter.class
)
public class Passenger {

    @EqualsAndHashCode.Include
    @GSheetColumn(name = "Passenger Id", cellStyle = @GSheetCellStyle(
            vAlign = VerticalAlignment.TOP,
            border = {BorderStyle.SOLID, BorderStyle.SOLID, BorderStyle.SOLID, BorderStyle.SOLID_MEDIUM}
    ))
    private Integer passengerId;

    @GSheetColumn(name = "Name")
    private String name;

    @GSheetColumn(name = "Sex")
    private String sex;

    @GSheetColumn(name = "Age", formatter = AgeFormatter.class)
    private Integer age;

    @GSheetColumn(name = "Survived", mapper = SurvivedFieldMapper.class, formatter = SurvivedFormatter.class)
    private boolean survived;

    @GSheetColumn(name = "Class", cellStyle = @GSheetCellStyle(
            hAlign = HorizontalAlignment.CENTER, border = BorderStyle.SOLID
    ))
    private Integer pClass;

    @GSheetColumn(name = "Siblings on board")
    private Integer SibSp;

    @GSheetColumn(name = "Parch")
    private Integer parch;

    @GSheetColumn(name = "Ticket")
    private String ticket;

    @GSheetColumn(name = "Fare")
    private Double fare;

    @GSheetColumn(name = "Cabin")
    private String cabin;

    @GSheetColumn(name = "Embarked", cellStyle = @GSheetCellStyle(
            vAlign = VerticalAlignment.TOP,
            border = {BorderStyle.SOLID, BorderStyle.SOLID_MEDIUM, BorderStyle.SOLID, BorderStyle.SOLID}
    ))
    private String embarked;
}
