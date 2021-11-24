package eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet;

import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SheetNotFound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Spreadsheet {

    private com.google.api.services.sheets.v4.model.Spreadsheet googleSpreadsheet;

    private int activeSheetIndex;

    private List<Request> requests;

    public Spreadsheet(com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet) {
        this.googleSpreadsheet = spreadsheet;
        activeSheetIndex = 0;
        requests = new ArrayList<>();
    }

    public List<String> getSheetNames() {
        return googleSpreadsheet.getSheets()
                .stream()
                .map(sheet -> sheet.getProperties().getTitle())
                .collect(Collectors.toList());
    }

    public Sheet getActiveSheet() {
        return new Sheet(googleSpreadsheet.getSheets().get(activeSheetIndex));
    }

    public Sheet selectSheet(String name) {
        List<com.google.api.services.sheets.v4.model.Sheet> list = googleSpreadsheet.getSheets();
        for (int i = 0; i <= list.size(); i++) {
            if (name.equals(list.get(i).getProperties().getTitle())) {
                activeSheetIndex = i;
                return new Sheet(googleSpreadsheet.getSheets().get(activeSheetIndex));
            }
        }
        throw new SheetNotFound("Sheet with this name wasn't found");
    }

    public Sheet selectSheet(int index) {
        index--;
        if (index < 0 || googleSpreadsheet.getSheets().size() <= index) {
            throw new SheetNotFound("Incorrect sheet id");
        }
        return new Sheet(googleSpreadsheet.getSheets().get(index));
    }

    public void rename(String name) {
        googleSpreadsheet.getProperties().setTitle(name);
        requests.add(new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(googleSpreadsheet.getProperties())
                        .setFields("*")
                        //make dictionary instead list
        ));
    }

    public String getName() {
        return googleSpreadsheet.getProperties().getTitle();
    }

    public Sheet cloneSheet(String sheetName) {
        com.google.api.services.sheets.v4.model.Sheet sheet = getSheet(sheetName);
        googleSpreadsheet.getSheets().add(sheet);
        return new Sheet(sheet.clone());
    }

    public void removeSheet(String sheetName){
        com.google.api.services.sheets.v4.model.Sheet sheet = getSheet(sheetName);
        googleSpreadsheet.getSheets().remove(sheet);
    }

    private com.google.api.services.sheets.v4.model.Sheet getSheet(String sheetName) {
        return googleSpreadsheet.getSheets()
                .stream()
                .filter(sheet -> sheetName.equals(sheet.getProperties().getTitle()))
                .findFirst()
                .orElseThrow(() -> new SheetNotFound("Sheet with this name not found"));
    }
}
