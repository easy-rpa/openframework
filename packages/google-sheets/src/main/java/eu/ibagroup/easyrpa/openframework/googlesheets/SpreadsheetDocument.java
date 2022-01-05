package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CopySheetToAnotherSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.DuplicateSheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.CopySheetException;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SheetNameAlreadyExist;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SheetNotFound;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.UpdateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpreadsheetDocument {

    private com.google.api.services.sheets.v4.model.Spreadsheet googleSpreadsheet;

    private int activeSheetIndex;
    private Sheets service;
    private List<Request> requests;
    private boolean isSessionOpened;
    private String sessionOwnerId;

    public SpreadsheetDocument(com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet, Sheets service) {
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
        return new Sheet(this, activeSheetIndex);
    }

    public Sheet selectSheet(String name) {
        List<com.google.api.services.sheets.v4.model.Sheet> list = googleSpreadsheet.getSheets();
        for (int i = 0; i < list.size(); i++) {
            if (name.equals(list.get(i).getProperties().getTitle())) {
                activeSheetIndex = i;
                return new Sheet(this, activeSheetIndex);
            }
        }
        throw new SheetNotFound(String.format("Sheet with name '%s' has not been found", name));
    }

    public Sheet selectSheet(int index) {
        if (index < 0 || googleSpreadsheet.getSheets().size() <= index) {
            throw new SheetNotFound("Incorrect sheet id");
        }
        activeSheetIndex = index;
        return new Sheet(this, activeSheetIndex);
    }

    public Sheet getSheetAt(int index) {
        if (index < 0 || googleSpreadsheet.getSheets().size() <= index) {
            throw new SheetNotFound("Incorrect sheet id");
        }
        return new Sheet(this);
    }

    public com.google.api.services.sheets.v4.model.Sheet getGSheetAt(int index) {
        if (index < 0 || googleSpreadsheet.getSheets().size() <= index) {
            throw new SheetNotFound("Incorrect sheet id");
        }
        return googleSpreadsheet.getSheets().get(index);
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
        commit();
    }

    public String getName() {
        return googleSpreadsheet.getProperties().getTitle();
    }

    public Sheet cloneSheet(String sheetName) {
        com.google.api.services.sheets.v4.model.Sheet sheet = getGSheet(sheetName).clone();

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

        BatchUpdateSpreadsheetResponse response = commit();
        SheetProperties properties = response
                .getReplies()
                .get(response.getReplies().size() - 1)
                .getDuplicateSheet()
                .getProperties();

        sheet.setProperties(properties);
        return new Sheet(this, newSheetIndex);
    }

    public void removeSheet(String sheetName) {
        com.google.api.services.sheets.v4.model.Sheet sheet = getGSheet(sheetName);
        googleSpreadsheet.getSheets().remove(sheet);

        requests.add(new Request().setDeleteSheet(
                new DeleteSheetRequest()
                        .setSheetId(sheet.getProperties().getSheetId())
        ));
        commit();
    }

    public List<Request> getRequests() {
        return this.requests;
    }

    public void setProperties(SpreadsheetProperties properties) {
        googleSpreadsheet.setProperties(properties);
    }

    public SpreadsheetProperties getProperties() {
        return googleSpreadsheet.getProperties();
    }

    public com.google.api.services.sheets.v4.model.Sheet getGSheet(String sheetName) {
        return googleSpreadsheet.getSheets()
                .stream()
                .filter(sheet -> sheetName.equals(sheet.getProperties().getTitle()))
                .findFirst()
                .orElseThrow(() -> new SheetNotFound("Sheet with this name not found"));
    }

    public BatchUpdateSpreadsheetResponse commit() {
        if (requests.size() > 0) {
            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);

            try {
                BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(googleSpreadsheet.getSpreadsheetId(), body).execute();
                requests.clear();
                return response;
            } catch (IOException e) {
                throw new UpdateException(e.getMessage());
            }
        }
        //return null if there were no updates
        return null;
    }

    public void openSessionIfRequired(String sessionOwnerId) {
        if (!isSessionOpened) {
            this.sessionOwnerId = sessionOwnerId;
            isSessionOpened = true;
        }
    }

    public void closeSessionIfRequired(String sessionOwnerId, List<Request> requests) {
        if (sessionOwnerId.equals(this.sessionOwnerId)) {
            this.requests = requests;
            commit();
            isSessionOpened = false;
        }
    }

    public String generateNewSessionId(){
        return (int) (Math.random() * 100) + "" + (System.currentTimeMillis() % 1000000);
    }

    public void copySheet(Sheet sheet) {
        CopySheetToAnotherSpreadsheetRequest requestBody = new CopySheetToAnotherSpreadsheetRequest();
        requestBody.setDestinationSpreadsheetId(googleSpreadsheet.getSpreadsheetId());
        try {
            service.spreadsheets().sheets().copyTo(sheet.getDocument().getId(), sheet.getId(), requestBody).execute();
        } catch (IOException e) {
            throw new CopySheetException(e.getMessage());
        }
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
