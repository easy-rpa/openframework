package eu.ibagroup.easyrpa.openframework.excel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Column implements Iterable<Cell> {

    private Sheet parent;

    private int columnIndex;

    protected Column(Sheet parent, int columnIndex) {
        this.parent = parent;
        this.columnIndex = columnIndex;
    }

    public ExcelDocument getDocument() {
        return parent.getDocument();
    }

    public Sheet getSheet() {
        return parent;
    }

    public int getIndex() {
        return columnIndex;
    }

    public CellRef getReference() {
        return new CellRef(0, columnIndex);
    }

    public Object getValue(String cellRef) {
        return getValue(new CellRef(cellRef), Object.class);
    }

    public <T> T getValue(String cellRef, Class<T> valueType) {
        return getValue(new CellRef(cellRef), valueType);
    }

    public Object getValue(CellRef cellRef) {
        return getValue(cellRef, Object.class);
    }

    public <T> T getValue(CellRef cellRef, Class<T> valueType) {
        return cellRef != null ? getValue(cellRef.getRow(), valueType) : null;
    }

    public Object getValue(int rowIndex) {
        return getValue(rowIndex, Object.class);
    }

    public <T> T getValue(int rowIndex, Class<T> valueType) {
        Cell cell = getCell(rowIndex);
        return cell != null ? cell.getValue(valueType) : null;
    }

    public void setValue(String cellRef, Object value) {
        setValue(new CellRef(cellRef), value);
    }

    public void setValue(CellRef cellRef, Object value) {
        if (cellRef != null) {
            setValue(cellRef.getRow(), value);
        }
    }

    public void setValue(int rowIndex, Object value) {
        Cell cell = getCell(rowIndex);
        if (cell == null) {
            cell = createCell(rowIndex);
        }
        cell.setValue(value);
    }

    public List<Object> getValues() {
        return getValues(Object.class);
    }

    public <T> List<T> getValues(Class<T> valueType) {
        return getRange(getFirstRowIndex(), getLastRowIndex(), valueType);
    }

    public void setValues(List<?> values) {
        putRange(0, values);
    }

    public List<Object> getRange(String startRef, String endRef) {
        return getRange(new CellRef(startRef), new CellRef(endRef));
    }

    public <T> List<T> getRange(String startRef, String endRef, Class<T> valueType) {
        return getRange(new CellRef(startRef), new CellRef(endRef), valueType);
    }

    public List<Object> getRange(CellRef startRef, CellRef endRef) {
        return getRange(startRef, endRef, Object.class);
    }

    public <T> List<T> getRange(CellRef startRef, CellRef endRef, Class<T> valueType) {
        return startRef != null && endRef != null ? getRange(startRef.getRow(), endRef.getRow(), valueType) : null;
    }

    public List<Object> getRange(int startRow, int endRow) {
        return getRange(startRow, endRow, Object.class);
    }

    public <T> List<T> getRange(int startRow, int endRow, Class<T> valueType) {
        List<T> values = new ArrayList<>();

        int r1 = Math.min(startRow, endRow);
        int r2 = Math.max(startRow, endRow);

        for (int row = r1; row <= r2; row++) {
            values.add(getValue(row, valueType));
        }
        return values;
    }

    public void putRange(String startRef, List<?> data) {
        putRange(new CellRef(startRef), data);
    }

    public void putRange(CellRef startRef, List<?> data) {
        if (startRef != null) {
            putRange(startRef.getRow(), data);
        }
    }

    public void putRange(int startRow, List<?> data) {
        if (data != null) {
            int row = startRow;
            for (Object cellValue : data) {
                setValue(row++, cellValue);
            }
        }
    }

    public Cell getCell(String cellRef) {
        return getCell(new CellRef(cellRef));
    }

    public Cell getCell(CellRef cellRef) {
        return cellRef != null ? getCell(cellRef.getRow()) : null;
    }

    public Cell getCell(int rowIndex) {
        if (rowIndex >= 0) {
            org.apache.poi.ss.usermodel.Row row = parent.getPoiSheet().getRow(rowIndex);
            if (row != null) {
                org.apache.poi.ss.usermodel.Cell cell = row.getCell(columnIndex);
                return cell != null ? new Cell(parent, rowIndex, columnIndex) : null;
            }
        }
        return null;
    }

    public Cell createCell(int rowIndex) {
        org.apache.poi.ss.usermodel.Sheet poiSheet = parent.getPoiSheet();
        org.apache.poi.ss.usermodel.Row poiRow = poiSheet.getRow(rowIndex);
        if (poiRow == null) {
            poiRow = poiSheet.createRow(rowIndex);
        }
        poiRow.createCell(columnIndex);
        return new Cell(parent, rowIndex, columnIndex);
    }

    public int getFirstRowIndex() {
        return parent.getPoiSheet().getFirstRowNum();
    }

    public int getLastRowIndex() {
        return parent.getPoiSheet().getLastRowNum();
    }

    public boolean isEmpty(){
        for(Cell cell : this){
            if(cell != null && !cell.isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator(parent.getPoiSheet());
    }

    private class CellIterator implements Iterator<Cell> {

        private org.apache.poi.ss.usermodel.Sheet poiSheet;
        private int index = 0;
        private int cellsCount;

        public CellIterator(org.apache.poi.ss.usermodel.Sheet poiSheet) {
            this.poiSheet = poiSheet;
            this.cellsCount = poiSheet.getLastRowNum() + 1;
        }

        @Override
        public boolean hasNext() {
            return index < cellsCount;
        }

        @Override
        public Cell next() {
            int rowIndex = index++;
            org.apache.poi.ss.usermodel.Row nextRow = poiSheet.getRow(rowIndex);
            if (nextRow != null) {
                org.apache.poi.ss.usermodel.Cell poiCell = nextRow.getCell(columnIndex);
                return poiCell != null ? new Cell(parent, rowIndex, columnIndex) : null;
            }
            return null;
        }
    }
}
