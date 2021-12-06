package eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet;

import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.model.Sheet;
import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.constants.InsertMethod;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.constants.MatchMethod;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.internal.GSheetElementsCache;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GSheet implements Iterable<Row> {

    private Spreadsheet parentSpreadsheet;

    private int sheetIndex;

    public GSheet(Spreadsheet parent, int sheetIndex) {
        this.sheetIndex = sheetIndex;
        this.parentSpreadsheet = parent;
    }

    public String getName() {
        return getGSheet().getProperties().getTitle();
    }

    public int getId() {
        return getGSheet().getProperties().getSheetId();
    }

    public Spreadsheet getParentSpreadsheet() {
        return parentSpreadsheet;
    }

    public int getIndex() {
        return getGSheet().getProperties().getIndex();
    }

    public void rename(String name) {
        getGSheet().getProperties().setTitle(name);

        parentSpreadsheet.getRequests().add(new Request().setUpdateSheetProperties(
                new UpdateSheetPropertiesRequest()
                        .setProperties(getGSheet().getProperties())
                        .setFields("*")
        ));
    }

    public GridData getData() {
        //why google sheet has list of grid data
        return getGSheet().getData().get(0);
    }

    public Cell getCell(String cellRef) {
        CellRef ref = new CellRef(cellRef);
        return getCell(ref.getRow(), ref.getCol());
    }

    public Cell getCell(int rowIndex, int colIndex) {
        Row row = getRow(rowIndex);
        return row != null ? row.getCell(colIndex) : null;
    }

    public Cell findCell(String value) {
        return findCell(MatchMethod.EXACT, value);
    }

    public Cell findCell(MatchMethod matchMethod, String value) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        for (Row row : this) {
            for (Cell cell : row) {
                if (matchMethod.match(cell.getValue(String.class), value)) {
                    return cell;
                }
            }
        }
        return null;
    }

    public Object getValue(String cellRef) {
        CellRef ref = new CellRef(cellRef);
        return getValue(ref.getRow(), ref.getCol(), Object.class);
    }

    public <T> T getValue(String cellRef, Class<T> valueType) {
        CellRef ref = new CellRef(cellRef);
        return getValue(ref.getRow(), ref.getCol(), valueType);
    }

    public Object getValue(int rowIndex, int colIndex) {
        return getValue(rowIndex, colIndex, Object.class);
    }

    public <T> T getValue(int rowIndex, int colIndex, Class<T> valueType) {
        Cell cell = getCell(rowIndex, colIndex);
        return cell != null ? cell.getValue(valueType) : null;
    }

    public void setValue(String cellRef, Object value) {
        CellRef ref = new CellRef(cellRef);
        setValue(ref.getRow(), ref.getCol(), value);
    }

    public void setValue(int rowIndex, int colIndex, Object value) {
        if (rowIndex >= 0 && colIndex >= 0) {
            Row row = getRow(rowIndex);
            if (row == null) {
                row = createRow(rowIndex);
            }
            row.setValue(colIndex, value);
        }
    }

    public List<List<Object>> getValues() {
        return getRange(getFirstRowIndex(), getFirstColumnIndex(), getLastRowIndex(), getLastColumnIndex());
    }

    public void setValues(List<List<?>> values) {
        putRange(0, 0, values);
    }

    public List<List<Object>> getRange(String startRef, String endRef) {
        CellRef sRef = new CellRef(startRef);
        CellRef eRef = new CellRef(endRef);
        return getRange(sRef.getRow(), sRef.getCol(), eRef.getRow(), eRef.getCol());
    }

    public List<List<Object>> getRange(int startRow, int startCol, int endRow, int endCol) {
        List<List<Object>> data = new ArrayList<>();

        if (startRow < 0 || startCol < 0 || endRow < 0 || endCol < 0) {
            return data;
        }

        int r1 = Math.min(startRow, endRow);
        int r2 = Math.max(startRow, endRow);
        int c1 = Math.min(startCol, endCol);
        int c2 = Math.max(startCol, endCol);

        for (int row = r1; row <= r2; row++) {
            List<Object> rowList = new ArrayList<>();
            for (int col = c1; col <= c2; col++) {
                rowList.add(getValue(row, col));
            }
            data.add(rowList);
        }
        return data;
    }

    public void putRange(String startRef, List<?> data) {
        CellRef sRef = new CellRef(startRef);
        putRange(sRef.getRow(), sRef.getCol(), data);
    }

    public void putRange(int startRow, int startCol, List<?> data) {
        if (data != null && data.size() > 0) {
            if (!(data.get(0) instanceof List)) {
                data = Collections.singletonList(data);
            }
            int rowIndex = startRow;
            for (Object rowList : data) {
                if (rowList instanceof List) {
                    Row row = getRow(rowIndex);
                    if (row == null) {
                        row = createRow(rowIndex);
                    }
                    row.putRange(startCol, (List<?>) rowList);
                    rowIndex++;
                }
            }
        }
    }

    public Row getRow(String rowRef) {
        return getRow(new CellRef(rowRef).getRow());
    }

    public Row getRow(int rowIndex) {
        if (rowIndex >= 0) {
            RowData row = getGSheet().getData().get(0).getRowData().get(rowIndex);
            return row != null ? new Row(this, rowIndex) : null;
        }
        return null;
    }

    public Row findRow(String... values) {
        return findRow(MatchMethod.EXACT, values);
    }

    public Row findRow(MatchMethod matchMethod, String... values) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        for (Row row : this) {
            boolean matchesFound = false;
            for (String key : values) {
                matchesFound = false;
                for (Cell cell : row) {
                    matchesFound = matchMethod.match(cell.getValue(String.class), key);
                    if (matchesFound) {
                        break;
                    }
                }
                if (!matchesFound) {
                    break;
                }
            }

            if (matchesFound) {
                return row;
            }
        }
        return null;
    }

    public Row createRow(int rowIndex) {
        getGSheet().getData().get(0).getRowData().add(rowIndex, new RowData());
        return new Row(this, rowIndex);
    }

    public void insertRows(InsertMethod method, String startCellRef, List<?> data) {
        CellRef ref = new CellRef(startCellRef);
        insertRows(method, ref.getRow(), ref.getCol(), data);
    }

    public void insertRows(InsertMethod method, int rowPos, int startCol, List<?> data) {
        if (rowPos < 0 || startCol < 0 || data == null || data.isEmpty()) {
            return;
        }

        int rowIndex = method == null || method == InsertMethod.BEFORE ? rowPos : rowPos + 1;

        if (rowIndex > getLastRowIndex()) {
            putRange(rowIndex, startCol, data);

        } else {
            if (!(data.get(0) instanceof List)) {
                data = Collections.singletonList(data);
            }
            shiftRows(rowIndex, data.size());
            putRange(rowIndex, startCol, data);
        }
    }

    public void removeRow(String rowRef) {
        removeRow(new CellRef(rowRef).getRow());
    }

    public void removeRow(Row row) {
        removeRow(row.getIndex());
    }

    public void removeRow(int rowIndex) {
        int lastRowIndex = getLastRowIndex();
        if (rowIndex < 0 || rowIndex > lastRowIndex) {
            return;
        }
        if (rowIndex == lastRowIndex) {
            com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
            RowData row = gSheet.getData().get(0).getRowData().get(rowIndex);
            if (row != null) {
                gSheet.getData().get(0).getRowData().remove(row);
            }
        } else {
            shiftRows(rowIndex + 1, -1);
        }
    }

    public void cleanRow(String rowRef) {
        cleanRow(new CellRef(rowRef).getRow());
    }

    public void cleanRow(Row row) {
        cleanRow(row.getIndex());
    }

    public void cleanRow(int rowIndex) {
        RowData row = getGSheet().getData().get(0).getRowData().get(rowIndex);
        if (row != null) {
            row.clear();
        }
    }

    public int getFirstRowIndex() {
        return getGSheet().getData().get(0).getStartRow();
    }

    //is this correct?
    public int getLastRowIndex() {
        GridData data = getGSheet().getData().get(0);
        return data.getStartRow() + data.getRowData().size() - 1;
    }

    public Column getColumn(String colRef) {
        return getColumn(new CellRef(colRef).getCol());
    }

    public Column getColumn(int colIndex) {
        return colIndex >= 0 ? new Column(this, colIndex) : null;
    }

    public void addColumn(List<?> values) {
        addColumn(0, values);
    }

    public void addColumn(String startRowRef, List<?> values) {
        addColumn(new CellRef(startRowRef).getRow(), values);
    }

    public void addColumn(int startRow, List<?> values) {
        List<List<?>> columnData = values.stream().map(Collections::singletonList).collect(Collectors.toList());
        putRange(startRow, getLastColumnIndex() + 1, columnData);
    }

    public void insertColumn(InsertMethod method, String columnRef, String startRowRef, List<?> data) {
        CellRef cRef = new CellRef(columnRef);
        CellRef srRef = new CellRef(startRowRef);
        insertColumn(method, cRef.getCol(), srRef.getRow(), data);
    }

    public void insertColumn(InsertMethod method, int columnPos, int startRow, List<?> values) {
        if (columnPos < 0 || startRow < 0 || values == null || values.isEmpty()) {
            return;
        }

        int columnIndex = method == null || method == InsertMethod.BEFORE ? columnPos : columnPos + 1;
        List<List<?>> columnData = values.stream().map(Collections::singletonList).collect(Collectors.toList());

        if (columnIndex <= getLastColumnIndex()) {
           // getParentSpreadsheet().runScript(new ColumnInsert(new CellRef(getName(), -1, columnIndex)));
        }
        putRange(startRow, columnIndex, columnData);
    }

    public void moveColumn(String columnToMoveRef, InsertMethod method, String toPositionRef) {
        moveColumn(new CellRef(columnToMoveRef).getCol(), method, new CellRef(toPositionRef).getCol());
    }

    public void moveColumn(String columnToMoveRef, InsertMethod method, int toPositionIndex) {
        moveColumn(new CellRef(columnToMoveRef).getCol(), method, toPositionIndex);
    }

    public void moveColumn(int columnToMoveIndex, InsertMethod method, String toPositionRef) {
        moveColumn(columnToMoveIndex, method, new CellRef(toPositionRef).getCol());
    }

    public void moveColumn(int columnToMoveIndex, InsertMethod method, int toPositionIndex) {
        if (columnToMoveIndex < 0 || columnToMoveIndex > getLastColumnIndex() || toPositionIndex < 0) {
            return;
        }
        int pos = method == null || method == InsertMethod.BEFORE ? toPositionIndex : toPositionIndex + 1;
        if (pos != columnToMoveIndex) {
            CellRange columnsRange = new CellRange(getName(), -1, columnToMoveIndex, -1, columnToMoveIndex);
            //getGSheet().runScript(new ColumnsMove(columnsRange, new CellRef(-1, pos)));
        }
    }

    public void removeColumn(String colRef) {
        removeColumn(new CellRef(colRef).getCol());
    }

    public void removeColumn(Column column) {
        removeColumn(column.getIndex());
    }

    public void removeColumn(int colIndex) {
        int lastColumnIndex = getLastColumnIndex();
        if (colIndex < 0 || colIndex > lastColumnIndex) {
            return;
        }
        if (colIndex == lastColumnIndex) {
            cleanColumn(colIndex);
        } else {
            CellRange columnsRange = new CellRange(getName(), -1, colIndex, -1, colIndex);
            //scripts??
            // getGSheet().runScript(new ColumnsDelete(columnsRange));
        }
    }

    public void cleanColumn(String colRef) {
        cleanColumn(new CellRef(colRef).getCol());
    }

    public void cleanColumn(Column column) {
        cleanColumn(column.getIndex());
    }

    public void cleanColumn(int colIndex) {
        for (RowData row : getGSheet().getData().get(0).getRowData()) {
            if (row != null) {
                CellData cell = row.getValues().get(colIndex);
                if (cell != null) {
                    row.remove(cell);
                }
            }
        }
    }

    public void setColumnWidth(String colRef, int width) {
        setColumnWidth(new CellRef(colRef).getCol(), width);
    }

    public void setColumnWidth(int columnIndex, int width) {
        if (width > 255) {
            throw new IllegalArgumentException("Column width cannot be more than 255.");
        }
        getGSheet().getData().get(0).getColumnMetadata().get(columnIndex).setPixelSize(width);
       // why *256 getGSheet().setColumnWidth(columnIndex, width * 256);
    }

    public int getFirstColumnIndex() {
        Sheet gSheet = getGSheet();
        int firstColIndex = -1;
        int firstRowNum = gSheet.getData().get(0).getStartRow();
        if (firstRowNum >= 0) {
            firstColIndex = Integer.MAX_VALUE;
            for (RowData row : gSheet.getData().get(0).getRowData()) {
                // ?? firstColIndex = Math.min(firstColIndex, row.getFirstCellNum());
            }
        }
        return  gSheet.getData().get(0).getStartColumn();
        //return firstColIndex;
    }

    public int getLastColumnIndex() {
        Sheet gSheet = getGSheet();
        int lastColIndex = -1;
        int firstRowNum = gSheet.getData().get(0).getStartRow();
        int firstColIndex = gSheet.getData().get(0).getStartColumn();
        if (firstRowNum >= 0) {
            for (RowData row : gSheet.getData().get(0).getRowData()) {
                //?? getlastrow index return last el?
                lastColIndex = Math.max(lastColIndex,firstColIndex+row.size()-1);
            }
        }
        return lastColIndex;
    }

    public <T> Table<T> getTable(String topLeftCellRef, Class<T> recordType) {
        CellRef ref = new CellRef(topLeftCellRef);
        return getTable(ref.getRow(), ref.getCol(), recordType);
    }

    public <T> Table<T> getTable(int headerTopRow, int headerLeftCol, Class<T> recordType) {
        return new Table<T>(this, headerTopRow, headerLeftCol, headerTopRow, getLastColumnIndex(), recordType);
    }

    public <T> Table<T> getTable(String headerTopLeftCellRef, String headerBottomRightCellRef, Class<T> recordType) {
        CellRef tlRef = new CellRef(headerTopLeftCellRef);
        CellRef brRef = new CellRef(headerBottomRightCellRef);
        return getTable(tlRef.getRow(), tlRef.getCol(), brRef.getRow(), brRef.getCol(), recordType);
    }

    public <T> Table<T> getTable(int headerTopRow, int headerLeftCol,
                                 int headerBottomRow, int headerRightCol, Class<T> recordType) {
        return new Table<T>(this, headerTopRow, headerLeftCol, headerBottomRow, headerRightCol, recordType);
    }

    /**
     * lookup table on the sheet with specified keywords in header.
     *
     * @param recordType - class instance of related records
     * @param keywords   - keywords to localize table header
     * @return instance of Table or <code>null</code> if nothing was found.
     */
    public <T> Table<T> findTable(Class<T> recordType, String... keywords) {
        return findTable(recordType, MatchMethod.EXACT, keywords);
    }

    /**
     * lookup table on the sheet with specified keywords in header.
     *
     * @param recordType  - class instance of related records
     * @param matchMethod - method of matching keywords with table column names
     * @param keywords    - keywords to localize table header
     * @return instance of Table or <code>null</code> if nothing was found.
     */
    public <T> Table<T> findTable(Class<T> recordType, MatchMethod matchMethod, String... keywords) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        Row headerRow = findRow(matchMethod, keywords);
        if (headerRow != null) {
            CellRef headerRef = headerRow.getReference();
            return getTable(headerRef.getRow(), headerRef.getCol(), recordType);
        }
        return null;
    }

    public <T> Table<T> insertTable(List<T> records) {
        return insertTable(0, 0, records);
    }

    public <T> Table<T> insertTable(String topLeftCellRef, List<T> records) {
        CellRef ref = new CellRef(topLeftCellRef);
        return insertTable(ref.getRow(), ref.getCol(), records);
    }

    public <T> Table<T> insertTable(int startRow, int startCol, List<T> records) {
        return startRow >= 0 && startCol >= 0 && records != null && records.size() > 0
                ? new Table<T>(this, startRow, startCol, records)
                : null;
    }

    public void addImage(String pathToImage, String positionRef) {
        addImage(pathToImage, positionRef, null);
    }

    //?????????
    public void addImage(String pathToImage, String fromCellRef, String toCellRef) {
        File imageFile = FilePathUtils.getFile(pathToImage);
        if (imageFile == null) {
            throw new IllegalArgumentException("Image path is not specified.");
        }
        try {
            addImage(new FileInputStream(imageFile), fromCellRef, toCellRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addImage(InputStream imageIS, String positionRef) {
        addImage(imageIS, positionRef, null);
    }

    public void addImage(InputStream imageIS, String fromCellRef, String toCellRef) {
        if (imageIS == null) {
            throw new IllegalArgumentException("Image input stream cannot be null.");
        }
        byte[] imageData;
        int imageFormat;
        try {
            imageData = IOUtils.toByteArray(imageIS);
            final FileMagic fm = FileMagic.valueOf(imageData);
            if (fm == FileMagic.PNG) {
                imageFormat = Workbook.PICTURE_TYPE_PNG;
            } else if (fm == FileMagic.JPEG) {
                imageFormat = Workbook.PICTURE_TYPE_JPEG;
            } else if (fm == FileMagic.EMF) {
                imageFormat = Workbook.PICTURE_TYPE_EMF;
            } else if (fm == FileMagic.WMF) {
                imageFormat = Workbook.PICTURE_TYPE_WMF;
            } else {
                throw new IllegalArgumentException("Unknown image file format. Only JPEG, PNG, EMF and WMF are supported.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        Workbook poiWb = poiSheet.getWorkbook();
        int pictureIdx = poiWb.addPicture(imageData, imageFormat);
        Drawing<?> drawing = poiSheet.createDrawingPatriarch();

        ClientAnchor anchor = poiWb.getCreationHelper().createClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

        CellRef fromRef = new CellRef(fromCellRef);
        anchor.setRow1(Math.max(fromRef.getRow(), 0));
        anchor.setCol1(Math.max(fromRef.getCol(), 0));
        if (toCellRef != null) {
            CellRef toRef = new CellRef(toCellRef);
            anchor.setRow2(Math.max(toRef.getRow(), 0));
            anchor.setCol2(Math.max(toRef.getCol(), 0));
        }

        Picture picture = drawing.createPicture(anchor, pictureIdx);
        if (toCellRef == null) {
            picture.resize();
        }
    }

    @Override
    public Iterator<Row> iterator() {
        return new RowIterator(getGSheet());
    }

    public com.google.api.services.sheets.v4.model.Sheet getGSheet() {
        return GSheetElementsCache.getGSheet(parentSpreadsheet.getId(), sheetIndex);
    }

    private void shiftRows(int startRow, int rowsCount) {
        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        int endRow = gSheet.getData().get(0).getRowData().size()-1;

        if (startRow < 0 || startRow > endRow) {
            return;
        }

        ///??
        gSheet.shiftRows(startRow, endRow, rowsCount);

        //Rows have been shifted and their positions changed. We need to cleanup
        // caches to get actual poi elements.
        GSheetElementsCache.clearRowsAndCellsCache(parentSpreadsheet.getId());

        // Shift data validation ranges separately since by default shifting of rows
        // doesn't affect position of data validation

        /// ????
        List<? extends DataValidation> dataValidations = gSheet.getData().get(0).getRowData();

        try {
            //Cleanup all data validations
            if (poiSheet instanceof XSSFSheet) {
                ((XSSFSheet) poiSheet).getCTWorksheet().unsetDataValidations();
            } else if (poiSheet instanceof SXSSFSheet) {
                XSSFSheet xssfSheet = TypeUtils.getFieldValue(poiSheet, "_sh");
                xssfSheet.getCTWorksheet().unsetDataValidations();
            } else if (poiSheet instanceof HSSFSheet) {
                TypeUtils.setFieldValue(((HSSFSheet) poiSheet).getSheet(), "_dataValidityTable", null);
            }
        } catch (Exception e) {
            // do nothing
        }

        for (DataValidation dv : dataValidations) {
            CellRangeAddressList regions = dv.getRegions();
            for (int i = 0; i < regions.countRanges(); i++) {
                CellRangeAddress dvRegion = regions.getCellRangeAddress(i);
                if (dvRegion.getFirstRow() >= startRow) {
                    dvRegion.setFirstRow(dvRegion.getFirstRow() + rowsCount);
                }
                if (dvRegion.getLastRow() >= startRow) {
                    dvRegion.setLastRow(dvRegion.getLastRow() + rowsCount);
                }
            }
            poiSheet.addValidationData(poiSheet.getDataValidationHelper().createValidation(dv.getValidationConstraint(), dv.getRegions()));
        }
    }

    private class RowIterator implements Iterator<Row> {

        private com.google.api.services.sheets.v4.model.Sheet gSheet;
        private int index = 0;
        private int rowsCount;

        public RowIterator(com.google.api.services.sheets.v4.model.Sheet gSheet) {
            this.gSheet = gSheet;
            this.rowsCount = gSheet.getData().get(0).getRowData().size();
        }

        @Override
        public boolean hasNext() {
            if (index < rowsCount) {
                RowData nextRow = gSheet.getData().get(0).getRowData().get(index);
                while (nextRow == null && index + 1 < rowsCount) {
                    nextRow = gSheet.getData().get(0).getRowData().get(++index);
                }
                return nextRow != null;
            }
            return false;
        }

        @Override
        public Row next() {
            return new Row(GSheet.this, index++);
        }
    }
}