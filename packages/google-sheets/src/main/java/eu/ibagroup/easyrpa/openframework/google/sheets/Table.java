package eu.ibagroup.easyrpa.openframework.google.sheets;

import eu.ibagroup.easyrpa.openframework.google.sheets.annotations.GSheetCellStyle;
import eu.ibagroup.easyrpa.openframework.google.sheets.annotations.GSheetColumn;
import eu.ibagroup.easyrpa.openframework.google.sheets.annotations.GSheetTable;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.InsertMethod;
import eu.ibagroup.easyrpa.openframework.google.sheets.internal.RecordTypeHelper;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents rectangle area of cells on the sheet where the top row or rows has titles of corresponding columns.
 * <p>
 * The table is defined by its header bounds (headers top row, left column, bottom row and right column) and the index
 * of the last row (bottom row). If bottom row is not specified then table ends at the last row of the sheet.
 * <p>
 * The content of table is represented with list of records. Record is an instance of POJO class where some fields are
 * mapped to corresponding table columns using {@link GSheetTable}
 * annotations. Each record corresponds to specific row of the table and indexed (0-based) starting with the row right
 * below the header bottom row.
 *
 * @param <T> type of table records. Should be POJO class with at least one field annotated with
 *            {@link GSheetTable}.
 * @see GSheetColumn
 * @see GSheetCellStyle
 */
public class Table<T> implements Iterable<T> {

    /**
     * Reference to parent sheet.
     */
    private Sheet parent;

    /**
     * Index of the header top row.
     */
    private int hTopRow;

    /**
     * Index of the header left column.
     */
    private int hLeftCol;

    /**
     * Index of the header bottom row.
     */
    private int hBottomRow;

    /**
     * Index of the header right column.
     */
    private int hRightCol;

    /**
     * Index of the last row of this table.
     */
    private int bottomRow = -1;

    /**
     * Cached map that maps column titles to its 0-based ordering number.
     */
    private Map<String, Integer> columnNameToIndexMap;

    /**
     * Cached list of table records.
     */
    private List<T> records;

    /**
     * Helper that converts records to corresponding row data and vice versa. Also it's responsible for providing
     * contained in record type meta information that necessary to build the table or its rows.
     */
    private RecordTypeHelper<T> typeHelper;

    /**
     * Builds a new table on the given sheet at position defined by <code>topRow</code> and <code>leftCol</code>
     * (top-left cell of the table).
     *
     * @param parent  parent sheet where the table should be placed.
     * @param topRow  0-based index of row that defines top-left cell of the table.
     * @param leftCol 0-based index of column that defines top-left cell of the table.
     * @param records list of records to insert into the table after creation. Also this list provides information
     *                about type of records with meta-information is necessary for table construction.
     *                That's why this list should not be empty.
     */
    @SuppressWarnings("unchecked")
    protected Table(Sheet parent, int topRow, int leftCol, List<T> records) {
        this.parent = parent;
        if (records != null && records.size() > 0) {
            this.typeHelper = RecordTypeHelper.getFor((Class<T>) records.get(0).getClass());
            buildTable(topRow, leftCol, records);
        } else {
            this.hTopRow = topRow;
            this.hLeftCol = leftCol;
            this.hBottomRow = topRow;
            this.hRightCol = parent.getLastColumnIndex();
        }
    }

    /**
     * Creates a new instance of table that is defined by existing cells range on specified sheet.
     *
     * @param parent          the parent sheet where this table is located.
     * @param headerTopRow    0-based index of table header top row.
     * @param headerLeftCol   0-based index of table left column.
     * @param headerBottomRow 0-based index of table header bottom row.
     * @param headerRightCol  0-based index of table right column.
     * @param recordType      class instance of records that this table should works with.
     */
    protected Table(Sheet parent, int headerTopRow, int headerLeftCol, int headerBottomRow, int headerRightCol, Class<T> recordType) {
        this.parent = parent;
        this.hTopRow = headerTopRow;
        this.hLeftCol = headerLeftCol;
        this.hBottomRow = headerBottomRow;
        this.hRightCol = headerRightCol;
        this.typeHelper = RecordTypeHelper.getFor(recordType);
    }

    /**
     * Gets parent sheet.
     *
     * @return parent sheet.
     */
    public Sheet getSheet() {
        return parent;
    }

    /**
     * Gets index of the top row of this table header.
     *
     * @return 0-based index of the top row of this table header.
     */
    public int getHeaderTopRow() {
        return hTopRow;
    }

    /**
     * Sets new index of the top row of this table header.
     *
     * @param topRowIndex the new index of the top row to set.
     */
    public void setHeaderTopRow(int topRowIndex) {
        this.hTopRow = topRowIndex;
        columnNameToIndexMap = null;
    }

    /**
     * Gets index of the left column of this table header.
     *
     * @return 0-based index of the left column  of this table header.
     */
    public int getHeaderLeftCol() {
        return hLeftCol;
    }

    /**
     * Sets new index of the left column of this table header.
     *
     * @param leftColIndex the new index of the left column to set.
     */
    public void setHeaderLeftCol(int leftColIndex) {
        this.hLeftCol = leftColIndex;
        columnNameToIndexMap = null;
    }

    /**
     * Gets index of the bottom row of this table header.
     *
     * @return 0-based index of the bottom row of this table header.
     */
    public int getHeaderBottomRow() {
        return hBottomRow;
    }

    /**
     * Sets new index of the bottom row of this table header.
     *
     * @param bottomRowIndex the new index of the bottom row to set.
     */
    public void setHeaderBottomRow(int bottomRowIndex) {
        this.hBottomRow = bottomRowIndex;
        columnNameToIndexMap = null;
    }

    /**
     * Gets index of the right column of this table header.
     *
     * @return 0-based index of the right column  of this table header.
     */
    public int getHeaderRightCol() {
        return hRightCol;
    }

    /**
     * Sets new index of the right column of this table header.
     *
     * @param rightColIndex the new index of the right column to set.
     */
    public void setHeaderRightCol(int rightColIndex) {
        this.hRightCol = rightColIndex;
        columnNameToIndexMap = null;
    }

    /**
     * Gets index of the last row of this table.
     * <p>
     * If the bottom row is not specified explicitly this method returns the actual index of the last row
     * of parent sheet.
     *
     * @return an actual index of the last row of this table.
     */
    public int getBottomRow() {
        if (bottomRow < 0) {
            return parent.getLastRowIndex();
        }
        return bottomRow;
    }

    /**
     * Sets index of the last row of this table explicitly.
     * <p>
     * It's necessary to use when the table ends earlier than the last row of the parent sheet. Once this bottom row
     * index is specified it will be automatically corrected during adding, inserting or removing records.
     *
     * @param bottomRowIndex 0-based index of the row that should be the last row of this table.
     */
    public void setBottomRow(int bottomRowIndex) {
        this.bottomRow = bottomRowIndex;
    }

    /**
     * Gets map that maps column titles to its ordering number (0-based).
     * <p>
     * It's necessary to properly map table records to corresponding row data and vice versa.
     *
     * @return map that maps column titles to its ordering number.
     */
    public Map<String, Integer> getColumnNameToIndexMap() {
        if (columnNameToIndexMap == null) {
            this.columnNameToIndexMap = getColumnNameToIndexMap(this.hTopRow, this.hLeftCol, this.hBottomRow, this.hRightCol);
        }
        return columnNameToIndexMap;
    }

    /**
     * Gets full list of records that are contained in this table.
     *
     * @return list of table records.
     */
    public List<T> getRecords() {
        if (records == null) {
            Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
            if (columnsIndexMap != null) {
                List<List<Object>> data = parent.getRange(hBottomRow + 1, hLeftCol, getBottomRow(), hRightCol);
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

    /**
     * Gets specific record by its index.
     * <p>
     * Records are indexed starting with the row right below the header bottom row.
     *
     * @param index 0-based index of the record to get.
     * @return instance of corresponding record or {@code null} if record at such index is not exist.
     */
    public T getRecord(int index) {
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

    /**
     * Iterates over all records starting from the first till the moment when <code>isSatisfy</code> is return
     * {@code true} for some record.
     *
     * @param isSatisfy lambda expression that accepts instance of each record and should return {@code true}
     *                  if the record satisfies to some condition.
     * @return instance of record for which <code>isSatisfy</code> returned {@code true} or {@code null}
     * if there are no such records.
     */
    public T findRecord(Predicate<T> isSatisfy) {
        int index = findRecordIndex(isSatisfy);
        return index >= 0 ? records.get(index) : null;
    }

    /**
     * Iterates over all records starting from the first till the moment when <code>isSatisfy</code> is return
     * {@code true} for some record.
     *
     * @param isSatisfy lambda expression that accepts instance of each record and should return {@code true}
     *                  if the record satisfies to some condition.
     * @return index of record for which <code>isSatisfy</code> returned {@code true} or <code>-1</code>
     * if there are no such records.
     */
    public int findRecordIndex(Predicate<T> isSatisfy) {
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

    /**
     * Gets index of given record.
     *
     * @param record instance of record to lookup.
     * @return index of given record or <code>-1</code> if record does not belong to this table.
     */
    public int indexOf(T record) {
        return record != null ? findRecordIndex(record::equals) : -1;
    }

    /**
     * Gets amount of records that contained in this table.
     *
     * @return amount of records of this table.
     */
    public int getRecordsCount() {
        return getBottomRow() - hBottomRow;
    }

    /**
     * Adds a new record at the end of this table.
     * <p>
     * The record will be converted to corresponding row data based on information specified within
     * {@link GSheetColumn} annotations. These annotations
     * also provide styling information that is applied after row adding.
     *
     * @param record the record to add.
     */
    public void addRecord(T record) {
        insertRecord(InsertMethod.BEFORE, getRecordsCount(), record);
    }

    /**
     * Adds list of records at the end of this table.
     * <p>
     * Each record will be converted to corresponding row data based on information specified within
     * {@link GSheetColumn} annotations. These annotations
     * also provide styling information that is applied after rows adding.
     *
     * @param records list of records to add.
     */
    public void addRecords(List<T> records) {
        insertRecords(InsertMethod.BEFORE, getRecordsCount(), records);
    }

    /**
     * Inserts new record into this table at given position.
     * <p>
     * The record will be converted to corresponding row data based on information specified within
     * {@link GSheetColumn} annotations. These annotations
     * also provide styling information that is applied after row inserting.
     *
     * @param method        defines position for insertion relatively to record specified by <code>relatedRecord</code>.
     * @param relatedRecord the record that identifies position for insertion.
     * @param record        the record to insert.
     */
    public void insertRecord(InsertMethod method, T relatedRecord, T record) {
        insertRecords(method, indexOf(relatedRecord), Collections.singletonList(record));
    }

    /**
     * Inserts new record into this table at given position.
     * <p>
     * The record will be converted to corresponding row data based on information specified within
     * {@link GSheetColumn} annotations. These annotations
     * also provide styling information that is applied after row inserting.
     *
     * @param method      defines position for insertion relatively to record index specified by <code>recordIndex</code>.
     * @param recordIndex the index of record that identifies position for insertion.
     * @param record      the record to insert.
     */
    public void insertRecord(InsertMethod method, int recordIndex, T record) {
        insertRecords(method, recordIndex, Collections.singletonList(record));
    }

    /**
     * Inserts list of records into this table at given position.
     * <p>
     * Each record will be converted to corresponding row data based on information specified within
     * {@link GSheetColumn} annotations. These annotations
     * also provide styling information that is applied after rows inserting.
     *
     * @param method        defines position for insertion relatively to record specified by <code>relatedRecord</code>.
     * @param relatedRecord the record that identifies position for insertion.
     * @param records       list of records to insert.
     */
    public void insertRecords(InsertMethod method, T relatedRecord, List<T> records) {
        insertRecords(method, indexOf(relatedRecord), records);
    }

    /**
     * Inserts list of records into this table at given position.
     * <p>
     * Each record will be converted to corresponding row data based on information specified within
     * {@link GSheetColumn} annotations. These annotations
     * also provide styling information that is applied after rows inserting.
     *
     * @param method      defines position for insertion relatively to record index specified by <code>recordIndex</code>.
     * @param recordIndex the index of record that identifies position for insertion.
     * @param records     list of records to insert.
     */
    public void insertRecords(InsertMethod method, int recordIndex, List<T> records) {
        if (recordIndex < 0 || records == null || records.isEmpty()) {
            return;
        }

        parent.getDocument().batchUpdate(request -> {
            Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
            Map<Integer, String> columnNamesMap = columnsIndexMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

            List<List<Object>> data = records.stream().map(r -> typeHelper.mapToValues(r, columnsIndexMap)).collect(Collectors.toList());
            parent.insertRows(method, recordIndex + hBottomRow + 1, hLeftCol, data);

            int insertPos = method == null || method == InsertMethod.BEFORE ? recordIndex : recordIndex + 1;
            if (this.records == null) {
                this.records = new ArrayList<>(Collections.nCopies(getRecordsCount(), null));
            }
            for (int i = insertPos, j = 0; j < records.size(); i++, j++) {
                this.records.set(i, records.get(j));
            }
            if (bottomRow >= 0) {
                bottomRow += records.size();
            }

            int rowsCount = data.size();
            int startRow = insertPos + hBottomRow + 1;
            for (int i = startRow; i < rowsCount + startRow; i++) {
                for (int j = hLeftCol; j <= hRightCol; j++) {
                    typeHelper.formatCell(parent.getCell(i, j), columnNamesMap.get(j - hLeftCol), i - hBottomRow - 1, this.records);
                }
            }
        });
    }

    /**
     * Transfers changes that have been done within given record to corresponding row of the sheet.
     *
     * @param record the record to update.
     */
    public void updateRecord(T record) {
        updateRecords(Collections.singletonList(record));
    }

    /**
     * Transfers changes that have been done within given records to corresponding rows of the sheet.
     *
     * @param records list of records to update.
     */
    public void updateRecords(List<T> records) {
        if (records == null) {
            return;
        }
        Map<String, Integer> columnsIndexMap = getColumnNameToIndexMap();
        Map<Integer, String> columnNamesMap = columnsIndexMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        parent.getDocument().batchUpdate(request -> {
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
        });
    }

    /**
     * Removes given record and corresponding row from this table.
     * <p>
     * Do nothing if the record is {@code null} or does not belong to this table.
     *
     * @param record the record to remove.
     */
    public void removeRecord(T record) {
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

    /**
     * Removes given records and corresponding rows from this table.
     *
     * @param records list of records to remove. Do nothing if this list is {@code null}.
     */
    public void removeRecords(List<T> records) {
        if (records != null) {
            for (T record : records) {
                removeRecord(record);
            }
        }
    }

    /**
     * Clears records cache.
     */
    public void clearCache() {
        records = null;
    }

    /**
     * Analyzes content of leading and trailing columns and if these columns are empty shifts indexes of left and
     * right columns to exclude them from this table bounds.
     */
    public void trimLeadingAndTrailingSpaces() {
        while (parent.getColumn(getHeaderLeftCol()).isEmpty()) {
            setHeaderLeftCol(getHeaderLeftCol() + 1);
        }

        while (parent.getColumn(getHeaderRightCol()).isEmpty()) {
            setHeaderRightCol(getHeaderRightCol() - 1);
        }
    }

    /**
     * Gets an iterator of records contained in this table.
     * <p>
     * This method allows using of this table object in "for" loop:
     * <pre>
     *     for(T record: table){
     *         ...
     *     }
     * </pre>
     *
     * @return on iterator of records contained in this table.
     */
    @Override
    public Iterator<T> iterator() {
        return new RecordIterator();
    }

    /**
     * Collects map that maps column titles to its ordering number (0-based).
     *
     * @param headerTopRow    0-based index of the top row to analyze on the parent sheet.
     * @param headerLeftCol   0-based index of the left column to analyze on the parent sheet.
     * @param headerBottomRow 0-based index of the bottom row to analyze on the parent sheet.
     * @param headerRightCol  0-based index of the right column to analyze on the parent sheet.
     * @return instance of map that maps column titles to its ordering number.
     */
    private Map<String, Integer> getColumnNameToIndexMap(int headerTopRow, int headerLeftCol, int headerBottomRow, int headerRightCol) {
        Map<String, Integer> columnsIndex = new HashMap<>();
        List<List<Object>> headerValues = parent.getRange(headerTopRow, headerLeftCol, headerBottomRow, headerRightCol);
        List<String> nameHierarchy = new ArrayList<>();
        for (int j = 0; j <= headerRightCol - headerLeftCol; j++) {
            for (int i = 0; i <= headerBottomRow - headerTopRow; i++) {
                if (i >= headerValues.size()) {
                    continue;
                }
                List<Object> rowValues = headerValues.get(i);
                if (rowValues == null || j >= rowValues.size()) {
                    continue;
                }
                String name = (String) rowValues.get(j);
                if (name != null && (nameHierarchy.isEmpty() || !name.equals(nameHierarchy.get(nameHierarchy.size() - 1)))) {
                    nameHierarchy.add(name);
                }
            }
            if (!nameHierarchy.isEmpty()) {
                String fullColumnName = nameHierarchy.stream().map(String::trim)
                        .collect(Collectors.joining(RecordTypeHelper.NAME_LEVEL_DELIMITER));
                columnsIndex.put(fullColumnName, j);
                nameHierarchy.clear();
            }
        }
        return columnsIndex.size() > 0 ? columnsIndex : null;
    }

    /**
     * Builds a new table at specified position.
     *
     * @param startRow 0-based index of row that defines top-left cell of the table to build.
     * @param startCol 0-based index of column that defines top-left cell of the table to build.
     * @param records  list of records to insert into the table after creation.
     */
    private void buildTable(int startRow, int startCol, List<T> records) {
        parent.getDocument().batchUpdate(r -> {
            RecordTypeHelper.ColumnNamesTree columnNamesTree = typeHelper.getColumnNames();

            hTopRow = startRow;
            hLeftCol = startCol;
            hBottomRow = startRow + columnNamesTree.getHeight() - 1;
            hRightCol = startCol + columnNamesTree.getWidth() - 1;

            List<List<String>> stub = Collections.nCopies(columnNamesTree.getHeight(),
                    Collections.nCopies(columnNamesTree.getWidth(), ""));
            parent.insertRows(InsertMethod.BEFORE, hTopRow, hLeftCol, stub);

            for (int i = 0; i < columnNamesTree.getHeight(); i++) {
                List<RecordTypeHelper.ColumnNameNode> columnNodes = columnNamesTree.getForLevel(i);
                Row row = parent.getRow(startRow + i);
                for (RecordTypeHelper.ColumnNameNode columnNode : columnNodes) {
                    Cell cell = row.getCell(startCol + columnNode.getColumnIndex());
                    cell.setValue(columnNode.getName());
                    if (columnNode.getWidth() > 1 || columnNode.getHeight() > 1) {
                        parent.mergeCells(cell.getRowIndex(), cell.getColumnIndex(),
                                cell.getRowIndex() + columnNode.getHeight() - 1,
                                cell.getColumnIndex() + columnNode.getWidth() - 1);
                    }
                    typeHelper.formatHeaderCell(cell, columnNode.getFullName());
                }
            }

            insertRecords(InsertMethod.BEFORE, 0, records);
        });
    }

    /**
     * Records iterator. Allows iteration over all existing records of this table using "for" loop.
     */
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
            return getRecord(index++);
        }
    }
}
