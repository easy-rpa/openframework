package eu.ibagroup.easyrpa.openframework.googlesheets;

import eu.ibagroup.easyrpa.openframework.googlesheets.constants.InsertMethod;
import eu.ibagroup.easyrpa.openframework.googlesheets.internal.RecordTypeHelper;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//TODO implement multi line header support. Currently only one line header is supported.
public class Table<T> implements Iterable<T> {

    private Sheet parent;

    private int hTopRow;
    private int hLeftCol;
    private int hBottomRow;
    private int hRightCol;

    private int bottomRow = -1;

    private Map<String, Integer> columnNameToIndexMap;
    private List<T> records;

    private RecordTypeHelper<T> typeHelper;

    @SuppressWarnings("unchecked")
    protected Table(Sheet parent, int topRow, int leftCol, List<T> records) throws IOException {
        this.parent = parent;
        if (records != null && records.size() > 0) {
            this.typeHelper = RecordTypeHelper.getFor((Class<T>) records.get(0).getClass());
            buildTable(topRow, leftCol, records);
        }
    }

    public Table(Sheet parent, int headerTopRow, int headerLeftCol, int headerBottomRow, int headerRightCol, Class<T> recordType) {
        this.parent = parent;
        this.hTopRow = headerTopRow;
        this.hLeftCol = headerLeftCol;
        this.hBottomRow = headerBottomRow;
        this.hRightCol = headerRightCol;
        this.typeHelper = RecordTypeHelper.getFor(recordType);
    }

    public Table(Sheet parent, int headerTopRow, int headerLeftCol, int headerBottomRow, int headerRightCol, int bottomRow, Class<T> recordType) {
        this.parent = parent;
        this.hTopRow = headerTopRow;
        this.hLeftCol = headerLeftCol;
        this.hBottomRow = headerBottomRow;
        this.hRightCol = headerRightCol;
        this.typeHelper = RecordTypeHelper.getFor(recordType);
        this.bottomRow = bottomRow;
    }

//DELETED will not support
//    public ExcelDocument getDocument() {
//        return parent.getDocument();
//    }

    public Sheet getSheet() {
        return parent;
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
        if (bottomRow < 0) {
            return parent.getLastRowIndex();
        }
        return bottomRow;
    }

    public void setBottomRow(int bottomRowIndex) {
        this.bottomRow = bottomRowIndex;
    }

    public Map<String, Integer> getColumnNameToIndexMap() throws IOException {
        if (columnNameToIndexMap == null) {
            this.columnNameToIndexMap = getColumnNameToIndexMap(this.hTopRow, this.hLeftCol, this.hRightCol);
        }
        return columnNameToIndexMap;
    }

    public List<T> getRecords() throws IOException {
        if (records == null) {
            Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
            if (columnsIndexMap != null) {
                List<List<Object>> data = parent.getRange(hBottomRow + 1, hLeftCol + 1, getBottomRow(), hRightCol + 1);
                records = data.stream().map(values -> typeHelper.mapToRecord(values, columnsIndexMap)).collect(Collectors.toList());
            }
        } else {
            //Make sure that all records have been loaded into cache
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i) == null) {
                    getRecord(i);
                }
            }
        }
        return new ArrayList<>(records);
    }

    public T getRecord(int index) throws IOException {
        int recordsCount = getRecordsCount();
        if (index < 0 || index >= recordsCount) {
            return null;
        }
        if (records == null) {
            records = new ArrayList<>(Collections.nCopies(recordsCount, null));
        }
        T record = records.get(index);
        if (record == null) {
            Row row = parent.getRow(index + hBottomRow + 1);
            List<Object> values = row.getRange(hLeftCol, hRightCol);
            record = typeHelper.mapToRecord(values, getColumnNameToIndexMap());
            records.set(index, record);
        }
        return record;
    }

    public T findRecord(Predicate<T> isSatisfy) throws IOException {
        int index = findRecordIndex(isSatisfy);
        return index >= 0 ? records.get(index) : null;
    }

    public int findRecordIndex(Predicate<T> isSatisfy) throws IOException {
        if (isSatisfy != null) {
            int recordsCount = getRecordsCount();
            for (int i = 0; i < recordsCount; i++) {
                T record = getRecord(i);
                if (isSatisfy.test(record)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int indexOf(T record) throws IOException {
        return record != null ? findRecordIndex(record::equals) : -1;
    }

    public int getRecordsCount() {
        return getBottomRow() - hBottomRow;
    }

    public void addRecord(T record) throws IOException {
        insertRecord(InsertMethod.BEFORE, getRecordsCount(), record);
    }

    public void addRecords(List<T> records) throws IOException {
        insertRecords(InsertMethod.BEFORE, getRecordsCount(), records);
    }

    public void insertRecord(InsertMethod method, T relatedRecord, T record) throws IOException {
        insertRecords(method, indexOf(relatedRecord), Collections.singletonList(record));
    }

    public void insertRecord(InsertMethod method, int recordIndex, T record) throws IOException {
        insertRecords(method, recordIndex, Collections.singletonList(record));
    }

    public void insertRecords(InsertMethod method, T relatedRecord, List<T> records) throws IOException {
        insertRecords(method, indexOf(relatedRecord), records);
    }

    public void insertRecords(InsertMethod method, int recordIndex, List<T> records) throws IOException {
        if (recordIndex < 0 || records == null || records.isEmpty()) {
            return;
        }
        Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
        //TODO think to replace map with list of column names
        Map<Integer, String> columnNamesMap = columnsIndexMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        List<List<Object>> data = records.stream().map(r -> typeHelper.mapToValues(r, columnsIndexMap)).collect(Collectors.toList());
        parent.insertRows(method, recordIndex + hBottomRow + 1, hLeftCol, data);

        int insertPos = method == null || method == InsertMethod.BEFORE ? recordIndex : recordIndex + 1;
        if (this.records == null) {
            this.records = new ArrayList<>(Collections.nCopies(getRecordsCount(), null));
        }
        this.records.addAll(insertPos, records);
        if (bottomRow >= 0) {
            bottomRow += records.size();
        }

        int rowsCount = data.size();
        int startRow = insertPos + hBottomRow + 1;
        for (int i = startRow; i < rowsCount + startRow; i++) {
            for (int j = hLeftCol; j < hRightCol; j++) {
                typeHelper.formatCell(parent.getCell(i, j), columnNamesMap.get(j - hLeftCol), i - hBottomRow - 1, this.records);
            }
        }
    }

    public void updateRecord(T record) throws IOException {
        updateRecords(Collections.singletonList(record));
    }

    public void updateRecords(List<T> records) throws IOException {
        if (records == null) {
            return;
        }
        Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
        //TODO think to replace map with list of column names
        Map<Integer, String> columnNamesMap = columnsIndexMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        for (T record : records) {
            int index = indexOf(record);
            if (index >= 0) {
                int rowNum = index + hBottomRow + 1;
                List<Object> values = typeHelper.mapToValues(record, columnsIndexMap);
                parent.putRange(rowNum, hLeftCol, values);
                for (int j = hLeftCol; j <= hRightCol; j++) {
                    typeHelper.formatCell(parent.getCell(rowNum, j), columnNamesMap.get(j - hLeftCol), index, this.records);
                }
                this.records.set(index, record);
            }
        }
    }

    public void removeRecord(T record) throws IOException {
        if (record != null) {
            int index = indexOf(record);
            if (index >= 0) {
                parent.removeRow(index + hBottomRow + 1);
                this.records.remove(index);
                if (bottomRow >= 0) {
                    bottomRow--;
                }
            }
        }
    }

    public void removeRecords(List<T> records) throws IOException {
        if (records != null) {
            for (T record : records) {
                removeRecord(record);
            }
        }
    }

    public void clearCache() {
        records = null;
    }

    public void trimLeadingAndTrailingSpaces() {
        while (parent.getColumn(getHeaderLeftCol()).isEmpty()) {
            setHeaderLeftCol(getHeaderLeftCol() + 1);
        }

        while (parent.getColumn(getHeaderRightCol()).isEmpty()) {
            setHeaderRightCol(getHeaderRightCol() - 1);
        }
    }
    /* TODO decide will we implement*/

/*    public Table<T> filter(int columnIndex, String filterPattern) {
        int lastColumnIndex = hRightCol - hLeftCol;
        if (columnIndex < 0 || columnIndex > lastColumnIndex) {
            return this;
        }
        CellRange tableRange = new CellRange(getSheet().getName(), hTopRow, hLeftCol, getBottomRow(), hRightCol);
        CellRange valuesRange = new CellRange(hBottomRow + 1, hLeftCol + columnIndex,
                getBottomRow(), hLeftCol + columnIndex);
        getDocument().runScript(new Filter(tableRange, columnIndex + 1, filterPattern, valuesRange));
        return this;
    }

    public Table<T> sort(int columnIndex, SortDirection direction) {
        int lastColumnIndex = hRightCol - hLeftCol;
        if (columnIndex < 0 || columnIndex > lastColumnIndex) {
            return this;
        }
        direction = direction != null ? direction : SortDirection.ASC;
        CellRange columnRange = new CellRange(getSheet().getName(), hTopRow, hLeftCol + columnIndex,
                getBottomRow(), hLeftCol + columnIndex);
        getDocument().runScript(new Sorter(columnRange, direction));
        return this;
    }*/

    @Override
    public Iterator<T> iterator() {
        return new RecordIterator();
    }

    private Map<String, Integer> getColumnNameToIndexMap(int tableHeaderRow, int headerLeftCol, int headerRightCol) throws IOException {
        Row headerRow = parent.getRow(tableHeaderRow);
        if (headerRow == null) {
            return null;
        }
        Map<String, Integer> columnsIndex = new HashMap<>();
        List<String> columns = headerRow.getRange(headerLeftCol, headerRightCol, String.class);
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i);
            columnsIndex.put(columnName != null ? columnName : "", i);
        }
        return columnsIndex;
    }

    private void buildTable(int startRow, int startCol, List<T> records) throws IOException {
        List<String> columnNames = typeHelper.getColumnNames();
        int columnsCount = columnNames.size();

        hTopRow = startRow;
        hLeftCol = startCol;
        hBottomRow = startRow + records.size() - 1;
        hRightCol = hLeftCol + columnsCount - 1;

        parent.insertRows(InsertMethod.BEFORE, hTopRow, hLeftCol, columnNames);
        for (int j = hLeftCol; j < hRightCol; j++) {
            typeHelper.formatHeaderCell(parent.getCell(hTopRow, j), columnNames.get(j - hLeftCol));
        }

        insertRecords(InsertMethod.BEFORE, 0, records);
    }

    private class RecordIterator implements Iterator<T> {

        private int index = 0;
        private int recordsCount;

        public RecordIterator() {
            recordsCount = getBottomRow() - hBottomRow;
        }

        @Override
        public boolean hasNext() {
            return index < recordsCount;
        }

        @Override
        public T next() {
            try {
                return getRecord(index++);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
