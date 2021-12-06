package eu.ibagroup.easyrpa.openframework.googlesheets;

import eu.ibagroup.easyrpa.openframework.googlesheets.internal.RecordTypeHelper;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Spreadsheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.utils.GSheetUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//TODO implement multi line header support. Currently only one line header is supported.
public class Table_bak<T> implements Iterable<T> {

    private GoogleSheets service;

    private String spreadsheetId;

    private int hTopRow;
    private int hLeftCol;
    private int hBottomRow;
    private int hRightCol;

    private int bottomRow = -1;

    private Map<String, Integer> columnNameToIndexMap;
    private List<T> records;

    private RecordTypeHelper<T> typeHelper;

    @SuppressWarnings("unchecked")
/*    protected Table(String spreadsheetId, int topRow, int leftCol, List<T> records, Sheets service) {
        this.spreadsheetId = spreadsheetId;
        if (records != null && records.size() > 0) {
            this.typeHelper = RecordTypeHelper.getFor((Class<T>) records.get(0).getClass());
            buildTable(topRow, leftCol, records);
        }
    }*/
    public Table_bak(GoogleSheets service, String spreadsheetId, int headerTopRow, int headerLeftCol, int headerBottomRow, int headerRightCol, int bottomRow, Class<T> recordType) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        this.hTopRow = headerTopRow;
        this.hLeftCol = headerLeftCol;
        this.hBottomRow = headerBottomRow;
        this.hRightCol = headerRightCol;
        this.bottomRow = bottomRow;
        this.typeHelper = RecordTypeHelper.getFor(recordType);
    }

    public Table_bak(GoogleSheets service, String spreadsheetId, int headerTopRow, int headerLeftCol, int headerBottomRow, int headerRightCol) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        this.hTopRow = headerTopRow;
        this.hLeftCol = headerLeftCol;
        this.hBottomRow = headerBottomRow;
        this.hRightCol = headerRightCol;
    }

    public Spreadsheet getDocument() throws IOException {
        return service.getSpreadsheet(spreadsheetId);
    }

    public int getHeaderTopRow() {
        return hTopRow;
    }

    public void setHeaderTopRow(int topRowIndex) {
        this.hTopRow = topRowIndex;
        columnNameToIndexMap = null;
    }

    public int getHeaderLeftCol() {
        return hLeftCol;
    }

    public void setHeaderLeftCol(int leftColIndex) {
        this.hLeftCol = leftColIndex;
        columnNameToIndexMap = null;
    }

    public int getHeaderBottomRow() {
        return hBottomRow;
    }

    public void setHeaderBottomRow(int bottomRowIndex) {
        this.hBottomRow = bottomRowIndex;
        columnNameToIndexMap = null;
    }

    public int getHeaderRightCol() {
        return hRightCol;
    }

    public void setHeaderRightCol(int rightColIndex) {
        this.hRightCol = rightColIndex;
        columnNameToIndexMap = null;
    }

    public int getBottomRow() {
//        if (bottomRow < 0) {
//            return parent.getActiveSheet().;
//        }
        return bottomRow;
    }

    public void setBottomRow(int bottomRowIndex) {
        this.bottomRow = bottomRowIndex;
    }

    public List<List<Object>> getdata() throws IOException {
        Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
        List<List<Object>> data = service.getValues(spreadsheetId, getRange()).getValues();
        records = data.stream().map(values -> typeHelper.mapToRecord(values, columnsIndexMap)).collect(Collectors.toList());
        return data;
    }

    public List<T> getRecords() throws IOException {
        if (records == null) {
            Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
            List<List<Object>> data = service.getValues(spreadsheetId, getRange()).getValues();
            if (columnsIndexMap != null) {
                records = data.stream().map(values -> typeHelper.mapToRecord(values, columnsIndexMap)).collect(Collectors.toList());
                return records;
            }
        } else {
            //Make sure that all records have been loaded into cache
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i) == null) {
//                    getRecord(i);
                }
            }
        }
        return new ArrayList<>(records);
    }

//    public T getRecord(int index) {
//        int recordsCount = getRecordsCount();
//        if (index < 0 || index >= recordsCount) {
//            return null;
//        }
//        if (records == null) {
//            records = new ArrayList<>(Collections.nCopies(recordsCount, null));
//        }
//        T record = records.get(index);
//        if (record == null) {
//            Row row = service.getRow(index + hBottomRow + 1);
//            List<Object> values = row.getRange(hLeftCol, hRightCol);
//            record = typeHelper.mapToRecord(values, getColumnNameToIndexMap());
//            records.set(index, record);
//        }
//        return record;
//    }

    public Map<String, Integer> getColumnNameToIndexMap() throws IOException {
        if (columnNameToIndexMap == null) {
            this.columnNameToIndexMap = getColumnNameToIndexMap(this.hTopRow, this.hLeftCol, this.hRightCol);
        }
        return columnNameToIndexMap;
    }

    private Map<String, Integer> getColumnNameToIndexMap(int tableHeaderRow, int headerLeftCol, int headerRightCol) throws IOException {
        Row headerRow = new Row(service, spreadsheetId, tableHeaderRow);
        Map<String, Integer> columnsIndex = new HashMap<>();
        List<String> columns = headerRow.getRange(headerLeftCol, headerRightCol, String.class);
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i);
            columnsIndex.put(columnName != null ? columnName : "", i);
        }
        return columnsIndex;
    }

    private String getRange(){
        return GSheetUtils.convertNumToColString(hLeftCol)+String.valueOf(hTopRow+1)+":"+GSheetUtils.convertNumToColString(hRightCol)+String.valueOf(bottomRow);
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
}
