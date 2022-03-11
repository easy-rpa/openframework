package eu.easyrpa.examples.google.sheets.spreadsheet_reading.entities;

import eu.easyrpa.examples.google.sheets.spreadsheet_reading.entities.mappers.SurvivedFieldMapper;
import eu.easyrpa.openframework.google.sheets.annotations.GSheetColumn;
import lombok.Data;

@Data
public class Passenger {

    @GSheetColumn(name = "Passenger Id")
    private Integer passengerId;

    @GSheetColumn(name = "Name")
    private String name;

    @GSheetColumn(name = "Sex")
    private String sex;

    @GSheetColumn(name = "Age")
    private Integer age;

    @GSheetColumn(name = "Survived", mapper = SurvivedFieldMapper.class)
    private boolean survived;

    @GSheetColumn(name = "Class")
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

    @GSheetColumn(name = "Embarked")
    private String embarked;
}
