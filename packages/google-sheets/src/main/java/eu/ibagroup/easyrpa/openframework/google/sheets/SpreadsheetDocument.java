package eu.ibagroup.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.MatchMethod;
import eu.ibagroup.easyrpa.openframework.google.sheets.exceptions.SpreadsheetException;
import eu.ibagroup.easyrpa.openframework.google.sheets.internal.SpreadsheetUpdateRequestsBatch;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents Google Spreadsheet document and provides functionality to work with it and its content.
 */
public class SpreadsheetDocument implements Iterable<Sheet> {

    /**
     * Reference to related Google Sheets service.
     */
    private Sheets sheetsService;

    /**
     * Reference to related Google Spreadsheet object.
     */
    private Spreadsheet spreadsheet;

    /**
     * Index of currently active sheet.
     */
    private int activeSheetIndex;

    /**
     * The batch used for collecting requests to update  this Spreadsheet document properties and data in Google Drive.
     */
    private SpreadsheetUpdateRequestsBatch updateRequestsBatch;

    /**
     * Creates a new Spreadsheet document for Google Spreadsheet with given ID.
     *
     * @param sheetsService instance of related Google Spreadsheets service.
     * @param spreadsheetId the file ID of source spreadsheet in Google Drive.
     * @throws SpreadsheetException in case of some errors during reading of spreadsheet with given ID from
     *                              Google Drive.
     */
    public SpreadsheetDocument(Sheets sheetsService, String spreadsheetId) {
        this.sheetsService = sheetsService;
        try {
            spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).setIncludeGridData(true).execute();
            activeSheetIndex = 0;
        } catch (IOException e) {
            throw new SpreadsheetException(String.format("Getting of spreadsheet with ID '%s' " +
                    "from Google Drive has failed.", spreadsheetId), e);
        }
    }

    /**
     * Gets unique identifier of this Spreadsheet document.
     *
     * @return unique identifier of this document.
     */
    public String getId() {
        return spreadsheet.getSpreadsheetId();
    }

    /**
     * Gets name for this Spreadsheet document.
     *
     * @return name for this Spreadsheet document.
     */
    public String getName() {
        return spreadsheet.getProperties().getTitle();
    }

    /**
     * Renames this Spreadsheet document.
     *
     * @param name the new name to set.
     */
    public void rename(String name) {
        if (name != null && !name.equals(spreadsheet.getProperties().getTitle())) {
            spreadsheet.getProperties().setTitle(name);
            batchUpdate(request -> request.addUpdateSpreadsheetPropertiesRequest(spreadsheet, "Title"));
        }
    }

    /**
     * Gets names of all sheets
     *
     * @return List of sheet names
     */
    public List<String> getSheetNames() {
        return spreadsheet.getSheets().stream()
                .map(sheet -> sheet.getProperties().getTitle())
                .collect(Collectors.toList());
    }

    /**
     * Creates a new sheet for this Spreadsheet document and return the high level
     * representation. New sheet will set as active sheet. Will set existing sheet
     * as active sheet and return it if sheet with name specified is exist already.
     *
     * @param sheetName The name to set for the sheet.
     * @return Sheet object representing the new sheet.
     */
    public Sheet createSheet(String sheetName) {
        Optional<com.google.api.services.sheets.v4.model.Sheet> sheet = spreadsheet.getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals(sheetName)).findFirst();
        if (sheet.isPresent()) {
            activeSheetIndex = sheet.get().getProperties().getIndex();
        } else {
            activeSheetIndex = spreadsheet.getSheets().size();
            com.google.api.services.sheets.v4.model.Sheet newSheet = new com.google.api.services.sheets.v4.model.Sheet();
            newSheet.setProperties(new SheetProperties().setIndex(activeSheetIndex).setTitle(sheetName));
            spreadsheet.getSheets().add(newSheet);

            SpreadsheetUpdateRequestsBatch request = new SpreadsheetUpdateRequestsBatch(this);
            request.addNewSheetRequest(newSheet);
            BatchUpdateSpreadsheetResponse response = request.send();
            newSheet.setProperties(response.getReplies().get(0).getAddSheet().getProperties());
        }
        return new Sheet(this, activeSheetIndex);
    }

    /**
     * Removes sheet with the given name. Does nothing if sheet with given name not
     * found.
     *
     * @param sheetName of the sheet to remove
     */
    public void removeSheet(String sheetName) {
        Optional<com.google.api.services.sheets.v4.model.Sheet> sheet = spreadsheet.getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals(sheetName)).findFirst();
        if (sheet.isPresent()) {
            //TODO get changes from response instead of manual changing
            spreadsheet.getSheets().remove(sheet.get());
            batchUpdate(request -> request.addDeleteSheetRequest(sheet.get().getProperties().getSheetId()));
        }
    }

    /**
     * Removes given sheet.
     *
     * @param sheet sheet to remove
     */
    public void removeSheet(Sheet sheet) {
        if (sheet.getDocument() == this) {
            //TODO get changes from response instead of manual changing
            spreadsheet.getSheets().remove(sheet.getIndex());
            batchUpdate(request -> request.addDeleteSheetRequest(sheet.getId()));
        }
    }

    /**
     * @return current active sheet.
     */
    public Sheet getActiveSheet() {
        return new Sheet(this, activeSheetIndex);
    }

    /**
     * Sets the sheet with given index as active and return it.
     *
     * @param index index of sheet that need to be activated.
     * @return instance of activated sheet or <code>null</code> if sheet with such index is absent.
     */
    public Sheet selectSheet(int index) {
        if (index >= 0 && index < spreadsheet.getSheets().size()) {
            activeSheetIndex = index;
            return new Sheet(this, activeSheetIndex);
        }
        return null;
    }

    /**
     * Sets the sheet with given name as active and return it.
     *
     * @param sheetName name of sheet that need to be activated.
     * @return instance of activated sheet or <code>null</code> if sheet not found.
     */
    public Sheet selectSheet(String sheetName) {
        Optional<com.google.api.services.sheets.v4.model.Sheet> sheet = spreadsheet.getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals(sheetName)).findFirst();
        if (sheet.isPresent()) {
            activeSheetIndex = sheet.get().getProperties().getIndex();
            return new Sheet(this, activeSheetIndex);
        }
        return null;
    }

    /**
     * Finds the sheet with a row that contains all given values and active it.
     *
     * @param values list of values to match.
     * @return instance of found and activated sheet or <code>null</code> if sheet not found.
     */
    public Sheet findSheet(String... values) {
        return findSheet(MatchMethod.EXACT, values);
    }

    /**
     * Finds the sheet with a row that contains all given values and active it.
     *
     * @param matchMethod method that defines how passed values are matched with each row values.
     * @param values      list of values to match.
     * @return instance of found and activated sheet or <code>null</code> if sheet not found.
     * @see MatchMethod
     */
    public Sheet findSheet(MatchMethod matchMethod, String... values) {
        int sheetIndex = 0;
        for (Sheet sheet : this) {
            Row row = sheet.findRow(matchMethod, values);
            if (row != null) {
                activeSheetIndex = sheetIndex;
                return sheet;
            }
            sheetIndex++;
        }
        return null;
    }

    /**
     * Gets underlay Google object representing this spreadsheet. This object can be used directly if some specific
     * functionality is necessary to use within RPA process.
     *
     * @return Google Spreadsheet object representing this spreadsheet.
     */
    public Spreadsheet getGSpreadsheet() {
        return spreadsheet;
    }

    /**
     * Gets Google Sheets service. This service can be used directly if some specific
     * functionality is necessary to use within RPA process.
     *
     * @return instance of Sheets service.
     */
    public Sheets getSheetsService() {
        return sheetsService;
    }

    /**
     * @return Spreadsheet document sheets iterator
     */
    @Override
    public Iterator<Sheet> iterator() {
        return new SheetIterator(spreadsheet.getSheets().size());
    }

    /**
     * TODO
     *
     * @param isAutoCommit
     */
    public void setAutoCommit(boolean isAutoCommit) {
        if (isAutoCommit) {
            this.commit();
        } else if (updateRequestsBatch == null) {
            updateRequestsBatch = new SpreadsheetUpdateRequestsBatch(this);
        }
    }

    /**
     * TODO
     */
    public void commit() {
        if (updateRequestsBatch != null) {
            updateRequestsBatch.send();
            updateRequestsBatch = null;
        }
    }

    /**
     * TODO
     *
     * @param action
     */
    public void withOneBatch(Consumer<SpreadsheetDocument> action) {
        boolean isRequestsBatchCreatedHere = false;

        if (updateRequestsBatch == null) {
            updateRequestsBatch = new SpreadsheetUpdateRequestsBatch(this);
            isRequestsBatchCreatedHere = true;
        }

        action.accept(this);

        if (isRequestsBatchCreatedHere) {
            updateRequestsBatch.send();
            updateRequestsBatch = null;
        }
    }

    /**
     * TODO
     *
     * @param action
     */
    protected void batchUpdate(Consumer<SpreadsheetUpdateRequestsBatch> action) {
        withOneBatch(doc -> {
            action.accept(updateRequestsBatch);
        });
    }

    /**
     * Sheets iterator. Allows iteration over all sheets present in Spreadsheet document using "for" loop.
     */
    private class SheetIterator implements Iterator<Sheet> {

        private int index = 0;
        private int sheetsCount;

        public SheetIterator(int sheetsCount) {
            this.sheetsCount = sheetsCount;
        }

        @Override
        public boolean hasNext() {
            return index < sheetsCount;
        }

        @Override
        public Sheet next() {
            return new Sheet(SpreadsheetDocument.this, index++);
        }
    }
}
