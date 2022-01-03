package eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading.entities;

import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetColumn;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Passenger {
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
    private String embarked;

    public List<Object> toObjectList() {
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

    Object getValue(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj;
    }
}
