package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.internal.GSpreadsheetDocumentElementsCache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private List<Request> requests = new ArrayList<>();

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

    public List<Request> setStyle(GSheetCellStyle newStyle) {
        return newStyle.applyTo(this, getDocument());
    }

    public List<Request> setStyle(GSheetCellStyle newStyle, CellRange cellRange) {
        return newStyle.applyTo(this, getDocument(), cellRange);
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

    public List<Request> setValue(Object value) {
        String sessionId = getDocument().generateNewSessionId();
        getDocument().openSessionIfRequired(sessionId);
        CellData googleCell = getGCell();
        if (value == null) {
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(""));
        } else if (value instanceof Date) {
            NumberFormat format = getStyle().getNumberFormat();
            if (!format.getType().equals(DATE_TYPE)) {
                format.setType(DATE_TYPE);
                format.setPattern(DEFAULT_DATE_FORMAT);
            }
            SimpleDateFormat formatter = new SimpleDateFormat(format.getPattern());
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(formatter.format((Date) value)));

        } else if (value instanceof Double) {
            googleCell.setUserEnteredValue(new ExtendedValue().setNumberValue((Double) value));
            NumberFormat format = getStyle().getNumberFormat();
            if (format.getType() == null || !format.getType().equals(NUMBER_TYPE)) {
                format.setType(NUMBER_TYPE);
            }
        } else if (value instanceof Boolean) {
            googleCell.setUserEnteredValue(new ExtendedValue().setBoolValue((Boolean) value));

        } else if (value instanceof String && value.toString().startsWith("=")) {
            googleCell.setUserEnteredValue(new ExtendedValue().setFormulaValue((String) value));

        } else {
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(value.toString()));
        }
        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(getDocument().getActiveSheet().getId())
                        .setStartRowIndex(this.getRowIndex())
                        .setEndRowIndex(this.getRowIndex() + 1)
                        .setStartColumnIndex(this.getColumnIndex())
                        .setEndColumnIndex(this.getColumnIndex() + 1)
                )
                .setCell(googleCell).setFields("userEnteredValue")));
        getDocument().closeSessionIfRequired(sessionId, requests);
        return requests;
    }

    public void setValue(Object value, CellRange cellRange) {
        String sessionId = getDocument().generateNewSessionId();
        getDocument().openSessionIfRequired(sessionId);
        CellData googleCell = getGCell();
        if (value == null) {
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(""));
        } else if (value instanceof Date) {
            NumberFormat format = getStyle().getNumberFormat();
            if (!format.getType().equals(DATE_TYPE)) {
                format.setType(DATE_TYPE);
                format.setPattern(DEFAULT_DATE_FORMAT);
            }
            SimpleDateFormat formatter = new SimpleDateFormat(format.getPattern());
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(formatter.format((Date) value)));

        } else if (value instanceof Double) {
            googleCell.setUserEnteredValue(new ExtendedValue().setNumberValue((Double) value));
            NumberFormat format = getStyle().getNumberFormat();
            if (!format.getType().equals(NUMBER_TYPE)) {
                format.setType(NUMBER_TYPE);
            }
        } else if (value instanceof Boolean) {
            googleCell.setUserEnteredValue(new ExtendedValue().setBoolValue((Boolean) value));

        } else if (value instanceof String && value.toString().startsWith("=")) {
            googleCell.setUserEnteredValue(new ExtendedValue().setFormulaValue((String) value));

        } else {
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(value.toString()));
        }
        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(getDocument().getActiveSheet().getId())
                        .setStartRowIndex(cellRange.getFirstRow())
                        .setEndRowIndex(cellRange.getLastRow())
                        .setStartColumnIndex(cellRange.getFirstCol())
                        .setEndColumnIndex(cellRange.getLastCol())
                )
                .setCell(googleCell).setFields("userEnteredValue")));
        getDocument().closeSessionIfRequired(sessionId, requests);
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

    public void setFormula(String newCellFormula, CellRange cellRange) {
        String sessionId = getDocument().generateNewSessionId();
        getDocument().openSessionIfRequired(sessionId);
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(getDocument().getActiveSheet().getId())
                                .setStartRowIndex(cellRange.getFirstRow())
                                .setEndRowIndex(cellRange.getLastRow())
                                .setStartColumnIndex(cellRange.getFirstCol())
                                .setEndColumnIndex(cellRange.getLastCol())
                        )
                        .setCell(getGCell()
                                .setUserEnteredValue(new ExtendedValue().setFormulaValue(newCellFormula)))
                        .setFields("userEnteredValue")));
        getDocument().closeSessionIfRequired(sessionId, requests);
    }

    public List<Request> setFormula(String newCellFormula) {
        String sessionId = getDocument().generateNewSessionId();
        getDocument().openSessionIfRequired(sessionId);
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(getDocument().getActiveSheet().getId())
                                .setStartRowIndex(this.getRowIndex())
                                .setEndRowIndex(this.getRowIndex() + 1)
                                .setStartColumnIndex(this.getColumnIndex())
                                .setEndColumnIndex(this.getColumnIndex() + 1)
                        )
                        .setCell(getGCell()
                                .setUserEnteredValue(new ExtendedValue().setFormulaValue(newCellFormula)))
                        .setFields("userEnteredValue")));
        getDocument().closeSessionIfRequired(sessionId, requests);
        return requests;
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
