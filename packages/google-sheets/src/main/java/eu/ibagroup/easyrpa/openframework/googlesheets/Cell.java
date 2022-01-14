package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.NumberFormat;
import eu.ibagroup.easyrpa.openframework.googlesheets.style.GSheetCellStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Cell {

    private static final String DATE_TYPE = "DATE";

    private static final String NUMBER_TYPE = "NUMBER";

    private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

    private String id;

    private Sheet parent;

    private String documentId;

    private int sheetIndex;

    private int rowIndex;

    private int columnIndex;

    protected Cell(Sheet parent, int rowIndex, int columnIndex) {
        this.parent = parent;
        this.documentId = parent.getDocument().getId();
        this.sheetIndex = parent.getIndex();
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.id = this.sheetIndex + "|" + this.rowIndex + "|" + this.columnIndex;
    }

    public SpreadsheetDocument getDocument() {
        return parent.getDocument();
    }

    public Sheet getSheet() {
        return parent;
    }

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
        newStyle.applyTo(this, getDocument());
    }

    public GSheetCellStyle getStyle() {
        return new GSheetCellStyle(this);
    }

    public Object getValue() {
        return getValue(Object.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> valueType) {
        if (String.class.isAssignableFrom(valueType)) {
            return (T) getValueAsString();
        } else if (Number.class.isAssignableFrom(valueType)) {
            return (T) getValueAsNumeric();
        }
        return (T) getTypedValue();
    }

    public void setValue(Object value) {
        boolean isSessionHasBeenOpened = false;
        try {
            if (!GSessionManager.isSessionOpened(getDocument())) {
                GSessionManager.openSession(getDocument());
                isSessionHasBeenOpened = true;
            }

            CellData googleCell = getGCell();
            if (value == null) {
                // poiCell.setBlank(); //todo check

            } else if (value instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(formatter.format((Date) value)));
                googleCell.setUserEnteredFormat(
                        new CellFormat().setNumberFormat(
                                new NumberFormat().setType("DATE").setPattern("dd.MM.yyyy")
                        )
                );

            } else if (value instanceof Double) {
                googleCell.setUserEnteredValue(new ExtendedValue().setNumberValue((Double) value));
                googleCell.setUserEnteredFormat(
                        new CellFormat().setNumberFormat(
                                new NumberFormat().setType("NUMBER")
                        )
                );
            } else if (value instanceof Boolean) {
                googleCell.setUserEnteredValue(new ExtendedValue().setBoolValue((Boolean) value));

            } else if (value instanceof String && value.toString().startsWith("=")) {
                googleCell.setUserEnteredValue(new ExtendedValue().setFormulaValue((String) value));

            } else {
                googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(value.toString()));
            }
            GSessionManager.getSession(getDocument()).addCellValueRequest(this, googleCell, getDocument());
        } finally {
            if (isSessionHasBeenOpened) {
                GSessionManager.closeSession(getDocument());
            }
        }
    }

    public boolean isEmpty() {
        CellData googleCell = getGCell();
        ExtendedValue value = googleCell.getUserEnteredValue();
        if (value.isEmpty())
            return true;
        //getFormulaEvaluate??

        return value.getNumberValue() == null && value.getFormulaValue() == null && value.getBoolValue() == null
                && value.getStringValue() == null && value.getErrorValue() == null;
    }

    public boolean hasFormula() {
        return getGCell().getUserEnteredValue().getFormulaValue() != null;
    }

    public String getFormula() {
        return getGCell().getUserEnteredValue().getFormulaValue();
    }

    public void setFormula(String newCellFormula) {
        boolean isSessionHasBeenOpened = false;
        try {
            if (!GSessionManager.isSessionOpened(getDocument())) {
                GSessionManager.openSession(getDocument());
                isSessionHasBeenOpened = true;
            }
            GSessionManager.getSession(getDocument()).addCellFormulaRequest(this, getGCell(), newCellFormula, getDocument());
        } finally {
            if (isSessionHasBeenOpened) {
                GSessionManager.closeSession(getDocument());
            }
        }
    }

    public CellData getGCell() {
       return GSpreadsheetDocumentElementsCache.getGCell(documentId, id, sheetIndex, rowIndex, columnIndex);
    }

    private Object getTypedValue() {
        CellData googleCell = getGCell();
        if (googleCell == null || googleCell.size() == 0) {
            return null;
        }
        Object value = null;
        ExtendedValue extendedValue = googleCell.getUserEnteredValue();

        if (extendedValue != null) {
            if (extendedValue.getNumberValue() != null) {
                value = extendedValue.getNumberValue();
            } else if (extendedValue.getFormulaValue() != null && !extendedValue.getFormulaValue().isEmpty()) {
                value = googleCell.getFormattedValue();
            } else if (extendedValue.getBoolValue() != null) {
                value = extendedValue.getBoolValue();
            } else if (!extendedValue.getStringValue().isEmpty()) {
                value = extendedValue.getStringValue();
            } else if (!extendedValue.getErrorValue().isEmpty()) {
                value = extendedValue.getErrorValue().getMessage();
            } else {
                value = extendedValue.toString();
            }
        } else {
            value = "";
        }
        return value;
    }

    private String getValueAsString() {
        CellData googleCell = getGCell();
        if (googleCell == null || googleCell.size() == 0) {
            return "";
        }
        ExtendedValue extendedValue = googleCell.getUserEnteredValue();
        if (extendedValue == null) {
            return "";
        }

        if (extendedValue.getNumberValue() != null) {
            return extendedValue.getNumberValue().toString();
        } else if (extendedValue.getFormulaValue() != null && !extendedValue.getFormulaValue().isEmpty()) {
            return googleCell.getFormattedValue();
        } else if (extendedValue.getBoolValue() != null) {
            return extendedValue.getBoolValue().toString();
        } else if (!extendedValue.getStringValue().isEmpty()) {
            return extendedValue.getStringValue();
        } else if (!extendedValue.getErrorValue().isEmpty()) {
            return extendedValue.getErrorValue().getMessage();
        }
        return extendedValue.getStringValue();
    }

    private Double getValueAsNumeric() {
        CellData googleCell = getGCell();
        if (googleCell == null || googleCell.size() == 0) {
            return null;
        }

        if (hasFormula()) {
            return googleCell.getEffectiveValue().getNumberValue();
        } else {
            return googleCell.getUserEnteredValue().getNumberValue();
        }
    }
}
