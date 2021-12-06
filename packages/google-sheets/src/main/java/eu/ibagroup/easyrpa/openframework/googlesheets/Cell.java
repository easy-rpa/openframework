package eu.ibagroup.easyrpa.openframework.googlesheets;


import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.util.Date;

//TODO Supporting of cell constraints (data validation)
public class Cell {

    private String id;

    //TODO decide which one is required
    private Spreadsheet parentDocument;
//    private Sheet parentSheet;

    private String documentId;

    private int sheetIndex;

    private int rowIndex;

    private int columnIndex;

    private GSheetCellStyle cellStyle;

    public Cell(Spreadsheet parent, int sheetIndex, int rowIndex, int columnIndex) {
        this.parentDocument = parent;
//        this.parentSheet = parent.getSheets().get(sheetIndex);
        this.documentId = parent.getSpreadsheetId();
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.id = this.sheetIndex + "|" + this.rowIndex + "|" + this.columnIndex;
    }

    public Cell(Spreadsheet spreadsheet, int id, String cell1Ref) {
    }

    public Spreadsheet getDocument() {
        return parentDocument;
    }

 //   public Sheet getSheet() {
 //       return parentSheet;
 //   }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public CellRef getReference() {
        return new CellRef(rowIndex, columnIndex);
    }

    public void setStyle(GSheetCellStyle newStyle) {
        cellStyle = newStyle;
    }

    public GSheetCellStyle getStyle() {
        return new GSheetCellStyle();
    }

    public Object getValue() {
        return getValue(Object.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> valueType) {
        if (String.class.isAssignableFrom(valueType)) {
            return (T) "val";
        } else if (Number.class.isAssignableFrom(valueType)) {
            return (T) Integer.valueOf(2);
        }
        return (T) "TODO";
    }

    public void setValue(Object value) {
        if (value == null) {

        } else if (value instanceof Date) {
            //TODO
        } else if (value instanceof Double) {
            //TODO
        } else if (value instanceof Boolean) {
            //TODO
        } else if (value instanceof String && value.toString().startsWith("=")) {
            //TODO
        } else {
            //TODO
        }
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean hasFormula() {
        return false;
    }

    public String getFormula() {
        //TODO
        return "";
    }

    public void setFormula(String newCellFormula) {
        //TODO
    }

}
