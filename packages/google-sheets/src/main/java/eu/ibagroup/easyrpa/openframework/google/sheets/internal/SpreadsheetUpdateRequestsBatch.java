package eu.ibagroup.easyrpa.openframework.google.sheets.internal;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.openframework.google.sheets.exceptions.SpreadsheetException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpreadsheetUpdateRequestsBatch {

    private static final int MAX_REQUESTS_IN_BATCH_COUNT = 100000;

    private List<Request> requests = new ArrayList<>();

    private SpreadsheetDocument document;

    public SpreadsheetUpdateRequestsBatch(SpreadsheetDocument document) {
        this.document = document;
    }

    public void addUpdateSpreadsheetPropertiesRequest(Spreadsheet spreadsheet, String... propertyNames) {
        requests.add(new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(spreadsheet.getProperties())
                        .setFields(String.join(",", propertyNames))
        ));
    }

    public void addNewSheetRequest(Sheet newSheet) {
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(newSheet.getProperties())));
    }

    public void addDeleteSheetRequest(int sheetId) {
        requests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId)));
    }

    public void addDuplicateSheetRequest(Sheet sourceSheet, int newSheetIndex, String newSheetName) {
        requests.add(new Request().setDuplicateSheet(new DuplicateSheetRequest()
                .setSourceSheetId(sourceSheet.getProperties().getSheetId())
                .setInsertSheetIndex(newSheetIndex).setNewSheetName(newSheetName)));
    }

    public void addUpdateSheetPropertiesRequest(Sheet sheet, String... propertyNames) {
        requests.add(new Request().setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                .setProperties(sheet.getProperties())
                .setFields(String.join(",", propertyNames))));
    }

    public void addMergeCellsRequest(GridRange range) {
        requests.add(new Request().setMergeCells(new MergeCellsRequest().setMergeType("MERGE_ALL").setRange(range)));
    }

    public void addUnmergeCellsRequest(GridRange range) {
        requests.add(new Request().setUnmergeCells(new UnmergeCellsRequest().setRange(range)));
    }

    public void addAppendRowsRequest(int rowsCount, int sheetId) {
        requests.add(new Request().setAppendDimension(new AppendDimensionRequest()
                .setDimension("ROWS").setLength(rowsCount).setSheetId(sheetId)));
    }

    public void addInsertRowsRequest(int startRow, int rowsCount, int sheetId) {
        DimensionRange range = new DimensionRange();
        range.setDimension("ROWS");
        range.setSheetId(sheetId);
        range.setStartIndex(startRow);
        range.setEndIndex(startRow + rowsCount);
        requests.add(new Request().setInsertDimension(new InsertDimensionRequest()
                .setRange(range).setInheritFromBefore(true)));
    }

    public void addDeleteRowsRequest(int startRow, int rowsCount, int sheetId) {
        DimensionRange range = new DimensionRange();
        range.setDimension("ROWS");
        range.setSheetId(sheetId);
        range.setStartIndex(startRow);
        range.setEndIndex(startRow + rowsCount);
        requests.add(new Request().setDeleteDimension(new DeleteDimensionRequest().setRange(range)));
    }

    public void addCleanRowRequest(int rowIndex, int sheetId) {
        GridRange range = new GridRange();
        range.setSheetId(sheetId);
        range.setStartRowIndex(rowIndex);
        range.setEndRowIndex(rowIndex + 1);
        requests.add(new Request().setDeleteRange(new DeleteRangeRequest()
                .setRange(range).setShiftDimension("COLUMNS")));
    }

    public void addAppendColumnsRequest(int columnsCount, int sheetId) {
        requests.add(new Request().setAppendDimension(new AppendDimensionRequest()
                .setDimension("COLUMNS").setLength(columnsCount).setSheetId(sheetId)));
    }

    public void addInsertColumnsRequest(int startCol, int columnsCount, int sheetId) {
        DimensionRange range = new DimensionRange();
        range.setDimension("COLUMNS");
        range.setSheetId(sheetId);
        range.setStartIndex(startCol);
        range.setEndIndex(startCol + columnsCount);
        requests.add(new Request().setInsertDimension(new InsertDimensionRequest()
                .setRange(range).setInheritFromBefore(true)));
    }

    public void addDeleteColumnsRequest(int startCol, int columnsCount, int sheetId) {
        DimensionRange range = new DimensionRange();
        range.setDimension("COLUMNS");
        range.setSheetId(sheetId);
        range.setStartIndex(startCol);
        range.setEndIndex(startCol + columnsCount);
        requests.add(new Request().setDeleteDimension(new DeleteDimensionRequest().setRange(range)));
    }

    public void addMoveColumnsRequest(int fromColIndex, int toColIndex, int columnsCount, int sheetId) {
        DimensionRange range = new DimensionRange();
        range.setDimension("COLUMNS");
        range.setSheetId(sheetId);
        range.setStartIndex(fromColIndex);
        range.setEndIndex(fromColIndex + columnsCount);
        requests.add(new Request().setMoveDimension(new MoveDimensionRequest()
                .setSource(range).setDestinationIndex(toColIndex)));
    }

    public void addCleanColumnRequest(int colIndex, int sheetId) {
        GridRange range = new GridRange();
        range.setSheetId(sheetId);
        range.setStartColumnIndex(colIndex);
        range.setEndColumnIndex(colIndex + 1);
        requests.add(new Request().setDeleteRange(new DeleteRangeRequest()
                .setRange(range).setShiftDimension("ROWS")));
    }

    public void addUpdateColumnMetadataRequest(int colIndex, DimensionProperties metadata, int sheetId) {
        DimensionRange range = new DimensionRange();
        range.setDimension("COLUMNS");
        range.setSheetId(sheetId);
        range.setStartIndex(colIndex);
        range.setEndIndex(colIndex + 1);
        requests.add(new Request().setUpdateDimensionProperties(new UpdateDimensionPropertiesRequest()
                .setRange(range).setProperties(metadata).setFields("*")));
    }

    public void addUpdateCellRequest(CellData cellData, int rowIndex, int colIndex, int sheetId, String... propertyNames) {
        addUpdateCellRequest(cellData, rowIndex, colIndex, sheetId, Arrays.asList(propertyNames));
    }

    public void addUpdateCellRequest(CellData cellData, int rowIndex, int colIndex, int sheetId, List<String> propertyNames) {
        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(sheetId)
                        .setStartRowIndex(rowIndex)
                        .setEndRowIndex(rowIndex + 1)
                        .setStartColumnIndex(colIndex)
                        .setEndColumnIndex(colIndex + 1)
                )
                .setCell(cellData)
                .setFields(String.join(",", propertyNames))));
    }

    public List<BatchUpdateSpreadsheetResponse> send() {
        List<BatchUpdateSpreadsheetResponse> responses = new ArrayList<>();
        if (requests.size() > 0) {
            try {
                int i = 0;
                List<Request> batch;
                while (i < requests.size() && (batch = requests.subList(i, i + Math.min(requests.size() - i, MAX_REQUESTS_IN_BATCH_COUNT))).size() > 0) {
                    i += batch.size();
                    BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(batch);
                    responses.add(document.getSheetsService().spreadsheets().batchUpdate(document.getId(), body).execute());
                }
            } catch (Exception e) {
                throw new SpreadsheetException("Batch update of spreadsheet has failed.", e);
            } finally {
                requests.clear();
                requests = null;
                document = null;
            }
        }
        return responses;
    }
}
