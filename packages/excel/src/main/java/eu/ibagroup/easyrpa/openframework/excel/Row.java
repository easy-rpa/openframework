package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO Supporting of styles coping per row (getting of while row style and applying to another row)
public class Row implements Iterable<Cell> {

    private String id;

    private Sheet parent;

    private int documentId;

    private int sheetIndex;

    private int rowIndex;

    protected Row(Sheet parent, int rowIndex) {
        this.parent = parent;
        this.documentId = parent.getDocument().getId();
        this.sheetIndex = parent.getIndex();
        this.rowIndex = rowIndex;
        this.id = this.sheetIndex + "|" + this.rowIndex;
    }

    public ExcelDocument getDocument() {
        return parent.getDocument();
    }

    public Sheet getSheet() {
        return parent;
    }

    public int getIndex() {
        return rowIndex;
    }

    public CellRef getReference() {
        return new CellRef(rowIndex, 0);
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
        return cellRef != null ? getValue(cellRef.getCol(), valueType) : null;
    }

    public Object getValue(int colIndex) {
        return getValue(colIndex, Object.class);
    }

    public <T> T getValue(int colIndex, Class<T> valueType) {
        Cell cell = getCell(colIndex);
        return cell != null ? cell.getValue(valueType) : null;
    }

    public void setValue(String cellRef, Object value) {
        setValue(new CellRef(cellRef), value);
    }

    public void setValue(CellRef cellRef, Object value) {
        if (cellRef != null) {
            setValue(cellRef.getCol(), value);
        }
    }

    public void setValue(int colIndex, Object value) {
        Cell cell = getCell(colIndex);
        if (cell == null) {
            cell = createCell(colIndex);
        }
        cell.setValue(value);
    }

    public List<Object> getValues() {
        return getValues(Object.class);
    }

    public <T> List<T> getValues(Class<T> valueType) {
        return getRange(getFirstCellIndex(), getLastCellIndex(), valueType);
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
        return startRef != null && endRef != null ? getRange(startRef.getCol(), endRef.getCol(), valueType) : null;
    }

    public List<Object> getRange(int startCol, int endCol) {
        return getRange(startCol, endCol, Object.class);
    }

    public <T> List<T> getRange(int startCol, int endCol, Class<T> valueType) {
        List<T> values = new ArrayList<>();

        int c1 = Math.min(startCol, endCol);
        int c2 = Math.max(startCol, endCol);

        for (int col = c1; col <= c2; col++) {
            values.add(getValue(col, valueType));
        }
        return values;
    }

    public void putRange(String startRef, List<?> data) {
        putRange(new CellRef(startRef), data);
    }

    public void putRange(CellRef startRef, List<?> data) {
        if (startRef != null) {
            putRange(startRef.getCol(), data);
        }
    }

    public void putRange(int startCol, List<?> data) {
        if (data != null) {
            int col = startCol;
            for (Object cellValue : data) {
                setValue(col++, cellValue);
            }
        }
    }

    public Cell getCell(String cellRef) {
        return getCell(new CellRef(cellRef));
    }

    public Cell getCell(CellRef cellRef) {
        return cellRef != null ? getCell(cellRef.getCol()) : null;
    }

    public Cell getCell(int colIndex) {
        if (colIndex >= 0) {
            org.apache.poi.ss.usermodel.Cell cell = getPoiRow().getCell(colIndex);
            return cell != null ? new Cell(parent, rowIndex, colIndex) : null;
        }
        return null;
    }

    public Cell createCell(int colIndex) {
        getPoiRow().createCell(colIndex);
        return new Cell(parent, rowIndex, colIndex);
    }

    public Cell addCell(Object value) {
        Cell cell = createCell(getLastCellIndex() + 1);
        cell.setValue(value);
        return cell;
    }

    public int getFirstCellIndex() {
        return getPoiRow().getFirstCellNum();
    }

    public int getLastCellIndex() {
        return getPoiRow().getLastCellNum();
    }

    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator(getPoiRow());
    }

    public org.apache.poi.ss.usermodel.Row getPoiRow() {
        return POIElementsCache.getPoiRow(documentId, id, sheetIndex, rowIndex);
    }

    private class CellIterator implements Iterator<Cell> {

        private org.apache.poi.ss.usermodel.Row poiRow;
        private int index = 0;
        private int cellsCount;

        public CellIterator(org.apache.poi.ss.usermodel.Row poiRow) {
            this.poiRow = poiRow;
            this.cellsCount = poiRow.getLastCellNum() + 1;
        }

        @Override
        public boolean hasNext() {
            if (index < cellsCount) {
                org.apache.poi.ss.usermodel.Cell nextCell = poiRow.getCell(index);
                while (nextCell == null && index + 1 < cellsCount) {
                    nextCell = poiRow.getCell(++index);
                }
                return nextCell != null;
            }
            return false;
        }

        @Override
        public Cell next() {
            return new Cell(parent, rowIndex, index++);
        }
    }
}
