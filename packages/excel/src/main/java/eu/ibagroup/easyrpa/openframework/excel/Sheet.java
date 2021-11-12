package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.InsertMethod;
import eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod;
import eu.ibagroup.easyrpa.openframework.excel.constants.SortDirection;

import java.util.List;
import java.util.function.Predicate;

public class Sheet {

    public String getName() {
        //TODO Implement this
        return null;
    }

    public Cell getCell(String cellRef) {
        //TODO Implement this
        return null;
    }

    public void insertHeader(String startCellRef, Class<?> recordClass) {
        //TODO Implement this
    }

    public void insertRecords(String startCellRef, List<?> data) {
        //TODO Implement this
    }

    public void setValue(String cellRef, Object value) {
        //TODO Implement this
    }

    /**
     * Gets data as list of rows from specified spreadsheet range. Every row is list
     * of typed cell values.
     *
     * @param startRef - reference to top left cell of the range to get data from.
     * @param endRef   - reference to bottom right cell of the range to get data
     *                 from.
     * @return data as list of lists
     */
    public List<List<Object>> getRange(String startRef, String endRef) {
        //TODO Implement this
        return null;
//        List<List<Object>> retData = new ArrayList<>();
//        CellReference refStart = new CellReference(startRef);
//        CellReference refEnd = new CellReference(endRef);
//
//        int r1 = Math.min(refStart.getRow(), refEnd.getRow());
//        int r2 = Math.max(refStart.getRow(), refEnd.getRow());
//        int c1 = Math.min(refStart.getCol(), refEnd.getCol());
//        int c2 = Math.max(refStart.getCol(), refEnd.getCol());
//
//        for (int row = r1; row <= r2; row++) {
//            List<Object> rowList = new ArrayList<>();
//            for (int col = c1; col <= c2; col++) {
//                rowList.add(getCellValue(new CellReference(row, col).formatAsString()));
//            }
//            retData.add(rowList);
//        }
//
//        return retData;
    }

    /**
     * Transfer data from list to the specified range of spreadsheet.
     *
     * @param startRef - left upper corner to start data transfer
     * @param data
     */
    public void putRange(String startRef, List<List<Object>> data) {
        //TODO Implement this
//        CellReference refStart = new CellReference(startRef);
//        int row = refStart.getRow();
//        int col = refStart.getCol();
//
//        for (List<Object> rowlist : data) {
//            for (Object cellValue : rowlist) {
//                String cellRef = new CellReference(row, col).formatAsString();
//                SpreadsheetUtil.setCellValue(findCell(cellRef), cellValue);
//                col++;
//            }
//            col = refStart.getCol();
//            row++;
//        }
    }

    public void updateRecord(String tableTopLeftCellRef, Object record) {
        //TODO Implement this
    }

    public void updateRecord(List<String> keywordsToLocalizeTable, Object record) {
        //TODO Implement this
    }

    public <T> T findRecord(List<String> keywordsToLocalizeTable, Predicate<T> isSatisfy) {
        //TODO Implement this
        return null;
    }

    /**
     * Exports sheet to PDF
     *
     * @param pdfFilePath
     */
    public void exportToPDF(String pdfFilePath) {
        //TODO Implement this
//        runScript(new ExportToPDF(getActiveSheet().getSheetName(), pdfFilePath));
    }

    public void addImage(String pathToImage, String fromCellRef, String toCellRef) {
        //TODO Implement this
    }

    public <T> List<T> getRecords(String tableTopLeftCellRef) {
        //TODO Implement this
        return null;
    }

    public <T> List<T> getRecords(List<String> keywordsToLocalizeTable) {
        //TODO Implement this
        return null;
    }

    public void addColumn(Column column) {
        //TODO Implement this
    }

    public void insertColumn(String positionRef, InsertMethod method, Column column) {
        //TODO Implement this
    }

    public void removeColumn(String columnRef) {
        //TODO Implement this
    }

    public void filterColumn(String columnRef, List<Object> valuesToFilter, MatchMethod exact) {
        //TODO Implement this
    }

    public Column getLostColumn() {
        //TODO Implement this
        return null;
    }

    public void moveColumn(String columnToMoveRef, String toPositionRef, InsertMethod method) {
        //TODO Implement this
    }

    public Column getColumn(String columnRef) {
        //TODO Implement this
        return null;
    }

    public void sortColumn(String columnRef, SortDirection direction) {
        //TODO Implement this
    }

    public Row getRow(int rowIndex) {
        //TODO Implement this
        return null;
    }

    public Row getRow(String rowRef) {
        //TODO Implement this
        return null;
    }

    public Row findRow(String... values) {
        //TODO Implement this
        return null;
    }

    public void addRow(Row row) {
        //TODO Implement this
    }

    public void insertRow(int position, InsertMethod method, Row row) {
        //TODO Implement this
    }

    public void insertRow(String positionRef, InsertMethod method, Row row) {
        //TODO Implement this
    }

    public void removeRow(int rowIndex) {
        //TODO Implement this
    }

    public void removeRow(Row row) {
        //TODO Implement this
    }

    public void cleanRow(int rowIndex) {
        //TODO Implement this
    }

    public void cleanRow(Row row) {
        //TODO Implement this
    }
}
