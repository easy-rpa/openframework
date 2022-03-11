package eu.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import eu.easyrpa.openframework.google.sheets.constants.MatchMethod;
import eu.easyrpa.openframework.google.sheets.exceptions.SpreadsheetException;
import eu.easyrpa.openframework.google.sheets.internal.SpreadsheetUpdateRequestsBatch;

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
     * Creates a new Spreadsheet document for Google Spreadsheet with given file ID.
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
            BatchUpdateSpreadsheetResponse response = request.send().get(0);
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
            spreadsheet.getSheets().remove(sheet.getIndex());
            batchUpdate(request -> request.addDeleteSheetRequest(sheet.getId()));
        }
    }

    /**
     * Gets current active sheet.
     *
     * @return object representing current active sheet.
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
     * Gets an actual Spreadsheet data from Google Drive.
     */
    public void reload() {
        try {
            spreadsheet = sheetsService.spreadsheets().get(spreadsheet.getSpreadsheetId()).setIncludeGridData(true).execute();
        } catch (IOException e) {
            throw new SpreadsheetException(String.format("Getting of spreadsheet with ID '%s' " +
                    "from Google Drive has failed.", spreadsheet.getSpreadsheetId()), e);
        }
    }

    /**
     * @return Spreadsheet document sheets iterator
     */
    @Override
    public Iterator<Sheet> iterator() {
        return new SheetIterator(spreadsheet.getSheets().size());
    }

    /**
     * Sets automatic sending of change requests to Google server on/off.
     * <p>
     * By default any change of spreadsheet data using SpreadsheetDocument or related objects (Sheet, Row, Cell etc.)
     * initiates sending of corresponding change requests to Google server. But when it's necessary to do a lot of
     * such changes it's possibly to face with limitation of Google quotes. Since no more than 60 requests can be done
     * per minute. See <a href="https://developers.google.com/sheets/api/limits">Sheets API Usage Limits</a> for more
     * details.
     * <p>
     * Using this method it's possible to switch off this behaviour and change requests won't be sent at all until
     * {@link #commit()} method is invoked. The same goal can be achieved using method {@link #withOneBatch(Consumer)}.
     * <pre>
     * ...
     * SpreadsheetDocument doc = ...;
     * ...
     * doc.setAutoCommit(false);
     * Sheet sheet = doc.getActiveSheet();
     * for(int i = 0; i < 100; i++){
     *  // No one actual request will be sent to Google server.
     *  // All changes will be done locally in memory.
     *  sheet.setValue(i, 0, "Value" + i);
     * }
     * // Commit initiate sending of previously done changes to Google server as one batch.
     * doc.commit();  //or doc.setAutoCommit(true);
     * ...
     * </pre>
     *
     * @param isAutoCommit {@code false} to switch off automatic sending of change requests to Google server and
     *                     {@code true} vice versa. In case of {@code true} value it's additionally calls
     *                     {@link #commit()} to insure that all previously done changes are sent to Google server.
     */
    public void setAutoCommit(boolean isAutoCommit) {
        if (isAutoCommit) {
            this.commit();
        } else if (updateRequestsBatch == null) {
            updateRequestsBatch = new SpreadsheetUpdateRequestsBatch(this);
        }
    }

    /**
     * Sends all previously done changes to Google server and gets back the actual content of this spreadsheet
     * after applying these changes on Google side.
     * <p>
     * This method is actually working only after calling of {@code setAutoCommit(false)} or within
     * {@link #withOneBatch(Consumer)}. In other cases it does nothing.
     */
    public void commit() {
        if (updateRequestsBatch != null) {
            updateRequestsBatch.send();
            reload();
            updateRequestsBatch = new SpreadsheetUpdateRequestsBatch(this);
        }
    }

    /**
     * Collects all changes are done within given lambda and sends them as one batch to Google server in the end.
     * <p>
     * Example:
     * <pre>
     * ...
     * doc.withOneBatch(d -> {
     *
     *   Sheet sheet = d.getActiveSheet();
     *   for(int i = 0; i < 100; i++){
     *       // No one actual request will be sent to Google server.
     *       // All changes will be done locally in memory.
     *       sheet.setValue(i, 0, "Value" + i);
     *   }
     *
     * }); // In the end of calling this method all done changes sent to Google
     *     // server as one batch and the actual content of this spreadsheet
     *     // is got back after applying these changes on Google side.
     * ...
     * </pre>
     *
     * @param action {@link Consumer<SpreadsheetDocument>} lambda expression that implements specific logic of changing
     *               this SpreadsheetDocument.
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
            reload();
        }
    }

    /**
     * Allows to put a set of spreadsheet update requests into one batch.
     *
     * @param action {@link Consumer} lambda expression with {@link SpreadsheetUpdateRequestsBatch} as argument. Within
     *               this lambda expression necessary update requests can be put into SpreadsheetUpdateRequestsBatch.
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
