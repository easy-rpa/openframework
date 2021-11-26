package eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SheetNameAlreadyExist;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SheetNotFound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Spreadsheet {

    private com.google.api.services.sheets.v4.model.Spreadsheet googleSpreadsheet;

    private int activeSheetIndex;

    private GoogleSheets service;

    //todo requests(update) via set
    private List<Request> requests;

    public Spreadsheet(com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet, GoogleSheets service) {
        this.googleSpreadsheet = spreadsheet;
        this.service = service;
        activeSheetIndex = 0;
        requests = new ArrayList<>();
    }

    public String getId() {
        return googleSpreadsheet.getSpreadsheetId();
    }

    public List<String> getSheetNames() {
        return googleSpreadsheet.getSheets()
                .stream()
                .map(sheet -> sheet.getProperties().getTitle())
                .collect(Collectors.toList());
    }

    public Sheet getActiveSheet() {
        return new Sheet(googleSpreadsheet.getSheets().get(activeSheetIndex), this);
    }

    public Sheet selectSheet(String name) {
        List<com.google.api.services.sheets.v4.model.Sheet> list = googleSpreadsheet.getSheets();
        for (int i = 0; i < list.size(); i++) {
            if (name.equals(list.get(i).getProperties().getTitle())) {
                activeSheetIndex = i;
                return new Sheet(googleSpreadsheet.getSheets().get(activeSheetIndex), this);
            }
        }
        throw new SheetNotFound("Sheet with this name wasn't found");
    }

    public Sheet selectSheet(int index) {
        index--;
        if (index < 0 || googleSpreadsheet.getSheets().size() <= index) {
            throw new SheetNotFound("Incorrect sheet id");
        }
        return new Sheet(googleSpreadsheet.getSheets().get(index), this);
    }

    public void rename(String name) {
        if (!sheetNameIsFree(name)) {
            throw new SheetNameAlreadyExist("This name is already exist in this spreadsheet");
        }
        googleSpreadsheet.getProperties().setTitle(name);

        requests.add(new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(googleSpreadsheet.getProperties())
                        .setFields("*")
        ));
        service.update(this);
    }

    public String getName() {
        return googleSpreadsheet.getProperties().getTitle();
    }

    public Sheet cloneSheet(String sheetName) {
        com.google.api.services.sheets.v4.model.Sheet sheet = getSheet(sheetName).clone();

        int newSheetIndex = googleSpreadsheet.getSheets().size();
        sheet.getProperties().setIndex(newSheetIndex);
        sheet.getProperties().setTitle(getClonedTitle(sheet.getProperties().getTitle()));

        googleSpreadsheet.getSheets().add(sheet);

        requests.add(new Request().setDuplicateSheet(
                new DuplicateSheetRequest()
                        .setNewSheetName(sheet.getProperties().getTitle())
                        .setInsertSheetIndex(newSheetIndex)
                        .setSourceSheetId(sheet.getProperties().getSheetId())
        ));

        BatchUpdateSpreadsheetResponse response = service.update(this);
        SheetProperties properties = response
                .getReplies()
                .get(response.getReplies().size() - 1)
                .getDuplicateSheet()
                .getProperties();

        return new Sheet(sheet.setProperties(properties), this);
    }

    public void removeSheet(String sheetName) {
        com.google.api.services.sheets.v4.model.Sheet sheet = getSheet(sheetName);
        googleSpreadsheet.getSheets().remove(sheet);

        requests.add(new Request().setDeleteSheet(
                new DeleteSheetRequest()
                        .setSheetId(sheet.getProperties().getSheetId())
        ));
        service.update(this);
    }

    public List<Request> getRequests() {
        return this.requests;
    }

    private com.google.api.services.sheets.v4.model.Sheet getSheet(String sheetName) {
        return googleSpreadsheet.getSheets()
                .stream()
                .filter(sheet -> sheetName.equals(sheet.getProperties().getTitle()))
                .findFirst()
                .orElseThrow(() -> new SheetNotFound("Sheet with this name not found"));
    }

    private String getClonedTitle(String title) {
        int index = 1;
        String str2Add;
        do {
            str2Add = "(" + index++ + ")";
        } while (sheetNameIsFree(title + str2Add));

        return title + str2Add;
    }

    private boolean sheetNameIsFree(String name) {
        return googleSpreadsheet.getSheets()
                .stream()
                .anyMatch(sheet -> name.equals(sheet.getProperties().getTitle()));
    }
}
